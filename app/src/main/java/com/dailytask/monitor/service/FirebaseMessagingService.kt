package com.dailytask.monitor.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.dailytask.monitor.DailyTaskApplication
import com.dailytask.monitor.R
import com.dailytask.monitor.ui.main.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Handle FCM messages here
        val notificationType = remoteMessage.data["type"]
        val title = remoteMessage.notification?.title ?: "DailyTask Monitor"
        val body = remoteMessage.notification?.body ?: "You have a new notification"
        
        when (notificationType) {
            "task_completed" -> {
                showSupervisorConfirmationNotification(title, body, remoteMessage.data)
            }
            "task_created" -> {
                showTaskCreatedNotification(title, body, remoteMessage.data)
            }
            "confirmation_request" -> {
                showConfirmationRequestNotification(title, body, remoteMessage.data)
            }
            else -> {
                showDefaultNotification(title, body)
            }
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Send token to your server or save it
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String) {
        // Implement token sending to your server
    }

    private fun showSupervisorConfirmationNotification(title: String, body: String, data: Map<String, String>) {
        val userId = data["userId"] ?: ""
        val taskId = data["taskId"] ?: ""
        
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("userId", userId)
            putExtra("taskId", taskId)
            putExtra("action", "confirm_task")
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, DailyTaskApplication.CHANNEL_ID_SUPERVISOR)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .addAction(
                R.drawable.ic_check,
                "Confirm",
                createConfirmationPendingIntent(userId, taskId, true)
            )
            .addAction(
                R.drawable.ic_close,
                "Reject",
                createConfirmationPendingIntent(userId, taskId, false)
            )
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun showTaskCreatedNotification(title: String, body: String, data: Map<String, String>) {
        val userId = data["userId"] ?: ""
        
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("userId", userId)
            putExtra("action", "view_tasks")
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, DailyTaskApplication.CHANNEL_ID_MAIN)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun showConfirmationRequestNotification(title: String, body: String, data: Map<String, String>) {
        val userId = data["userId"] ?: ""
        val taskId = data["taskId"] ?: ""
        
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("userId", userId)
            putExtra("taskId", taskId)
            putExtra("action", "confirm_task")
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, DailyTaskApplication.CHANNEL_ID_MAIN)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun showDefaultNotification(title: String, body: String) {
        val notification = NotificationCompat.Builder(this, DailyTaskApplication.CHANNEL_ID_MAIN)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun createConfirmationPendingIntent(userId: String, taskId: String, confirm: Boolean): PendingIntent {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("userId", userId)
            putExtra("taskId", taskId)
            putExtra("confirm", confirm)
            putExtra("action", "confirmation_action")
        }

        return PendingIntent.getActivity(
            this,
            if (confirm) 1 else 2,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}