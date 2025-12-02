package com.dailytask.monitor

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging

class DailyTaskApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        
        // Create notification channels
        createNotificationChannels()
        
        // Subscribe to FCM topics
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                // Save token to shared preferences or send to server
            }
        }
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Main notification channel
            val mainChannel = NotificationChannel(
                CHANNEL_ID_MAIN,
                "Daily Task Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for daily tasks and reminders"
                enableLights(true)
                enableVibration(true)
                setShowBadge(true)
            }

            // Alarm notification channel
            val alarmChannel = NotificationChannel(
                CHANNEL_ID_ALARM,
                "Task Alarms",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Critical task alarms that cannot be dismissed"
                enableLights(true)
                enableVibration(true)
                setShowBadge(true)
            }

            // Supervisor notification channel
            val supervisorChannel = NotificationChannel(
                CHANNEL_ID_SUPERVISOR,
                "Supervisor Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for supervisor confirmations"
                enableLights(true)
                enableVibration(true)
            }

            notificationManager.createNotificationChannels(listOf(mainChannel, alarmChannel, supervisorChannel))
        }
    }

    companion object {
        const val CHANNEL_ID_MAIN = "daily_task_channel"
        const val CHANNEL_ID_ALARM = "alarm_channel"
        const val CHANNEL_ID_SUPERVISOR = "supervisor_channel"
    }
}