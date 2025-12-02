package com.dailytask.monitor.ui.tasks

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dailytask.monitor.data.model.Task

@Composable
fun TaskListScreen(
    viewModel: TaskViewModel = hiltViewModel()
) {
    val tasks by viewModel.tasks.collectAsState()
    val showAddDialog by viewModel.showAddDialog.collectAsState()
    
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showAddDialog() }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (tasks.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Text("لا توجد مهام حالياً")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(tasks) { task ->
                        TaskItem(
                            task = task,
                            onComplete = { viewModel.completeTask(task) },
                            onDelete = { viewModel.deleteTask(task) }
                        )
                    }
                }
            }
        }
    }
    
    if (showAddDialog) {
        AddTaskDialog(
            onDismiss = { viewModel.hideAddDialog() },
            onAddTask = { task ->
                viewModel.addTask(task)
                viewModel.hideAddDialog()
            }
        )
    }
}

@Composable
fun TaskItem(
    task: Task,
    onComplete: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium
                )
                
                TaskStatusChip(status = task.status)
            }
            
            if (task.description.isNotEmpty()) {
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "الوقت: ${task.time}",
                    style = MaterialTheme.typography.bodySmall
                )
                
                Text(
                    text = "الأولوية: ${getUrgencyText(task.urgencyLevel)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (task.status == Task.TaskStatus.PENDING) {
                    Button(
                        onClick = onComplete,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("تم التنفيذ")
                    }
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                OutlinedButton(
                    onClick = onDelete,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("حذف")
                }
            }
        }
    }
}

@Composable
fun TaskStatusChip(status: Task.TaskStatus) {
    val (text, color) = when (status) {
        Task.TaskStatus.PENDING -> "قيد الانتظار" to MaterialTheme.colorScheme.secondary
        Task.TaskStatus.IN_PROGRESS -> "قيد التنفيذ" to MaterialTheme.colorScheme.primary
        Task.TaskStatus.COMPLETED -> "مكتمل" to MaterialTheme.colorScheme.tertiary
        Task.TaskStatus.CONFIRMED -> "مؤكد" to MaterialTheme.colorScheme.primaryContainer
    }
    
    AssistChip(
        onClick = { },
        label = { Text(text) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = color
        )
    )
}

fun getUrgencyText(level: Task.UrgencyLevel): String {
    return when (level) {
        Task.UrgencyLevel.LOW -> "منخفضة"
        Task.UrgencyLevel.MEDIUM -> "متوسطة"
        Task.UrgencyLevel.HIGH -> "عالية"
        Task.UrgencyLevel.CRITICAL -> "حرجة"
    }
}