package com.dailytask.monitor.ui.alarm

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dailytask.monitor.ui.theme.DailyTaskMonitorTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlarmActivity : ComponentActivity() {

    companion object {
        const val EXTRA_TASK_ID = "extra_task_id"
        const val EXTRA_USER_ID = "extra_user_id"
        const val EXTRA_TASK_TITLE = "extra_task_title"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Make activity show on lock screen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
        
        val taskId = intent.getStringExtra(EXTRA_TASK_ID) ?: ""
        val userId = intent.getStringExtra(EXTRA_USER_ID) ?: ""
        val taskTitle = intent.getStringExtra(EXTRA_TASK_TITLE) ?: "Task Alarm"
        
        setContent {
            DailyTaskMonitorTheme {
                AlarmScreen(
                    taskId = taskId,
                    userId = userId,
                    taskTitle = taskTitle,
                    onDismiss = {
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun AlarmScreen(
    taskId: String,
    userId: String,
    taskTitle: String,
    viewModel: AlarmViewModel = hiltViewModel(),
    onDismiss: () -> Unit
) {
    val confirmationState by viewModel.confirmationState.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.initialize(taskId, userId)
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "تنبيه المهمة!",
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        Text(
            text = taskTitle,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 48.dp)
        )
        
        when (confirmationState) {
            is ConfirmationState.WaitingForUser -> {
                Button(
                    onClick = { viewModel.markTaskCompleted() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Text("تم تنفيذ المهمة")
                }
            }
            
            is ConfirmationState.WaitingForSupervisor -> {
                CircularProgressIndicator(
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    text = "في انتظار تأكيد المراقب...",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            
            is ConfirmationState.Confirmed -> {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Confirmed",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                
                Text(
                    text = "تم تأكيد المهمة!",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(top = 16.dp)
                )
                
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.padding(top = 32.dp)
                ) {
                    Text("موافق")
                }
            }
            
            is ConfirmationState.Rejected -> {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = "Rejected",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                
                Text(
                    text = "رفض المراقب التأكيد",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(top = 16.dp)
                )
                
                Text(
                    text = "يجب إعادة تنفيذ المهمة",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
                )
                
                Button(
                    onClick = { viewModel.markTaskCompleted() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("إعادة التنفيذ")
                }
            }
        }
    }
}

@Composable
fun rememberViewModel(): AlarmViewModel {
    return hiltViewModel()
}