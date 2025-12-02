package com.dailytask.monitor.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.dailytask.monitor.data.model.Task
import com.dailytask.monitor.receiver.AlarmReceiver
import java.text.SimpleDateFormat
import java.util.*

object AlarmScheduler {

    fun scheduleDailyAlarm(context: Context, userId: String, startTime: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        val calendar = Calendar.getInstance().apply {
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val date = timeFormat.parse(startTime)
            
            time = date ?: Date()
            set(Calendar.HOUR_OF_DAY, get(Calendar.HOUR_OF_DAY))
            set(Calendar.MINUTE, get(Calendar.MINUTE))
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            
            // If the time has already passed today, schedule for tomorrow
            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1)
            }
        }

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(AlarmReceiver.EXTRA_TASK_ID, "daily_alarm")
            putExtra(AlarmReceiver.EXTRA_TASK_TITLE, "Time to plan your day!")
            putExtra(AlarmReceiver.EXTRA_USER_ID, userId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            DAILY_ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Cancel any existing alarm
        alarmManager.cancel(pendingIntent)

        // Schedule the alarm
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }

    fun scheduleTaskAlarm(context: Context, task: Task) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        val calendar = Calendar.getInstance().apply {
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val date = timeFormat.parse(task.time)
            
            time = date ?: Date()
            set(Calendar.HOUR_OF_DAY, get(Calendar.HOUR_OF_DAY))
            set(Calendar.MINUTE, get(Calendar.MINUTE))
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            
            // If the time has already passed today, schedule for tomorrow
            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1)
            }
        }

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(AlarmReceiver.EXTRA_TASK_ID, task.taskId)
            putExtra(AlarmReceiver.EXTRA_TASK_TITLE, task.title)
            putExtra(AlarmReceiver.EXTRA_USER_ID, task.userId)
        }

        val requestCode = task.taskId.hashCode()
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Cancel any existing alarm for this task
        alarmManager.cancel(pendingIntent)

        // Schedule the alarm
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }

    fun cancelTaskAlarm(context: Context, task: Task) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        val intent = Intent(context, AlarmReceiver::class.java)
        val requestCode = task.taskId.hashCode()
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
    }

    private const val DAILY_ALARM_REQUEST_CODE = 1001
}