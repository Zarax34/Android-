package com.dailytask.monitor.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.dailytask.monitor.utils.AlarmScheduler
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Reschedule alarms after device reboot
            rescheduleAlarms(context)
        }
    }

    private fun rescheduleAlarms(context: Context) {
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        
        if (user != null) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val database = FirebaseDatabase.getInstance()
                    val userSnapshot = database.reference
                        .child("users")
                        .child(user.uid)
                        .get()
                        .await()
                    
                    val startTime = userSnapshot.child("startTime").value as? String ?: "06:00"
                    
                    // Schedule daily alarm
                    AlarmScheduler.scheduleDailyAlarm(context, user.uid, startTime)
                    
                    // Reschedule any pending tasks
                    val tasksSnapshot = database.reference
                        .child("tasks")
                        .child(user.uid)
                        .get()
                        .await()
                    
                    for (taskSnapshot in tasksSnapshot.children) {
                        val task = taskSnapshot.getValue(com.dailytask.monitor.data.model.Task::class.java)
                        if (task != null && task.status == com.dailytask.monitor.data.model.Task.TaskStatus.PENDING) {
                            AlarmScheduler.scheduleTaskAlarm(context, task)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}