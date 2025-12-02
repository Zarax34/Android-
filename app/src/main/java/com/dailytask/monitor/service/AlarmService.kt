package com.dailytask.monitor.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.app.NotificationCompat
import com.dailytask.monitor.DailyTaskApplication
import com.dailytask.monitor.R
import com.dailytask.monitor.ui.alarm.AlarmActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AlarmService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    private var alarmJob: Job? = null
    private var isAlarmPlaying = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val taskId = intent?.getStringExtra(EXTRA_TASK_ID) ?: ""
        val taskTitle = intent?.getStringExtra(EXTRA_TASK_TITLE) ?: "Task Alarm"
        val userId = intent?.getStringExtra(EXTRA_USER_ID) ?: ""

        startForeground(NOTIFICATION_ID, createNotification(taskTitle, taskId, userId))
        startAlarm()

        return START_STICKY
    }

    private fun createNotification(taskTitle: String, taskId: String, userId: String): Notification {
        val notificationIntent = Intent(this, AlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(AlarmActivity.EXTRA_TASK_ID, taskId)
            putExtra(AlarmActivity.EXTRA_USER_ID, userId)
            putExtra(AlarmActivity.EXTRA_TASK_TITLE, taskTitle)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, DailyTaskApplication.CHANNEL_ID_ALARM)
            .setContentTitle("Task Alarm")
            .setContentText(taskTitle)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setOngoing(true)
            .setAutoCancel(false)
            .setFullScreenIntent(pendingIntent, true)
            .build()
    }

    private fun startAlarm() {
        if (isAlarmPlaying) return

        isAlarmPlaying = true

        // Start vibration
        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        startVibration()

        // Start sound
        val alarmUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)

        mediaPlayer = MediaPlayer().apply {
            setDataSource(this@AlarmService, alarmUri)
            setLooping(true)
            setVolume(1.0f, 1.0f)
            prepare()
            start()
        }

        // Keep the service alive
        alarmJob = CoroutineScope(Dispatchers.IO).launch {
            while (isAlarmPlaying) {
                delay(5000) // Keep alive every 5 seconds
            }
        }
    }

    private fun startVibration() {
        vibrator?.let { vibrator ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createWaveform(
                        longArrayOf(0, 1000, 500, 1000),
                        0
                    )
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(longArrayOf(0, 1000, 500, 1000), 0)
            }
        }
    }

    fun stopAlarm() {
        isAlarmPlaying = false
        
        alarmJob?.cancel()
        alarmJob = null

        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
            mediaPlayer = null
        }

        vibrator?.cancel()
        vibrator = null

        stopForeground(true)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAlarm()
    }

    companion object {
        const val EXTRA_TASK_ID = "extra_task_id"
        const val EXTRA_TASK_TITLE = "extra_task_title"
        const val EXTRA_USER_ID = "extra_user_id"
        private const val NOTIFICATION_ID = 1
    }
}