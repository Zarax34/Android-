package com.dailytask.monitor.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.dailytask.monitor.service.AlarmService

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getStringExtra(EXTRA_TASK_ID) ?: ""
        val taskTitle = intent.getStringExtra(EXTRA_TASK_TITLE) ?: "Task Alarm"
        val userId = intent.getStringExtra(EXTRA_USER_ID) ?: ""

        // Start the alarm service
        val serviceIntent = Intent(context, AlarmService::class.java).apply {
            putExtra(AlarmService.EXTRA_TASK_ID, taskId)
            putExtra(AlarmService.EXTRA_TASK_TITLE, taskTitle)
            putExtra(AlarmService.EXTRA_USER_ID, userId)
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }

    companion object {
        const val EXTRA_TASK_ID = "extra_task_id"
        const val EXTRA_TASK_TITLE = "extra_task_title"
        const val EXTRA_USER_ID = "extra_user_id"
    }
}