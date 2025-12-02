package com.dailytask.monitor.ui.tasks

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dailytask.monitor.data.model.Task
import java.util.*

@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onAddTask: (Task) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("09:00") }
    var urgencyLevel by remember { mutableStateOf(Task.UrgencyLevel.MEDIUM) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("إضافة مهمة جديدة") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("عنوان المهمة") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("الوصف (اختياري)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    maxLines = 3
                )
                
                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("الوقت (HH:MM)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    singleLine = true
                )
                
                Text(
                    text = "مستوى الإلحاح:",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Task.UrgencyLevel.values().forEach { level ->
                        FilterChip(
                            selected = urgencyLevel == level,
                            onClick = { urgencyLevel = level },
                            label = { Text(getUrgencyText(level)) },
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && time.isNotBlank()) {
                        val task = Task(
                            taskId = UUID.randomUUID().toString(),
                            userId = "current_user_id", // This should be set from ViewModel
                            title = title,
                            description = description,
                            time = time,
                            urgencyLevel = urgencyLevel
                        )
                        onAddTask(task)
                    }
                },
                enabled = title.isNotBlank() && time.isNotBlank()
            ) {
                Text("إضافة")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}