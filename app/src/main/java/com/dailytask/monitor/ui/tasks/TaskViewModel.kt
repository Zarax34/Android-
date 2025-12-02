package com.dailytask.monitor.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dailytask.monitor.data.model.Task
import com.dailytask.monitor.data.repository.AuthRepository
import com.dailytask.monitor.data.repository.TaskRepository
import com.dailytask.monitor.utils.AlarmScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()
    
    private val _showAddDialog = MutableStateFlow(false)
    val showAddDialog: StateFlow<Boolean> = _showAddDialog.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadTasks()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUser()?.userId ?: return@launch
            
            taskRepository.getTasksFlow(userId).collect { tasks ->
                _tasks.value = tasks
            }
        }
    }

    fun addTask(task: Task) {
        viewModelScope.launch {
            _isLoading.value = true
            
            val currentUser = authRepository.getCurrentUser()
            if (currentUser != null) {
                val taskWithUserId = task.copy(userId = currentUser.userId)
                
                val result = taskRepository.createTask(taskWithUserId)
                if (result.isSuccess) {
                    // Schedule alarm for the task
                    AlarmScheduler.scheduleTaskAlarm(
                        getApplication(), // This needs to be handled properly
                        taskWithUserId
                    )
                    
                    // Send notification to supervisor if linked
                    if (currentUser.linkedSupervisorId != null) {
                        sendTaskCreatedNotification(currentUser.linkedSupervisorId!!, taskWithUserId)
                    }
                }
            }
            
            _isLoading.value = false
        }
    }

    fun completeTask(task: Task) {
        viewModelScope.launch {
            _isLoading.value = true
            
            val updatedTask = task.copy(
                status = Task.TaskStatus.COMPLETED,
                completedAt = System.currentTimeMillis()
            )
            
            val result = taskRepository.updateTask(updatedTask)
            if (result.isSuccess) {
                // Cancel the alarm
                AlarmScheduler.cancelTaskAlarm(
                    getApplication(), // This needs to be handled properly
                    updatedTask
                )
                
                // Send completion notification to supervisor
                val currentUser = authRepository.getCurrentUser()
                if (currentUser?.linkedSupervisorId != null) {
                    sendTaskCompletionNotification(currentUser.linkedSupervisorId!!, updatedTask)
                }
            }
            
            _isLoading.value = false
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            _isLoading.value = true
            
            val result = taskRepository.deleteTask(task.userId, task.taskId)
            if (result.isSuccess) {
                // Cancel the alarm
                AlarmScheduler.cancelTaskAlarm(
                    getApplication(), // This needs to be handled properly
                    task
                )
            }
            
            _isLoading.value = false
        }
    }

    fun showAddDialog() {
        _showAddDialog.value = true
    }

    fun hideAddDialog() {
        _showAddDialog.value = false
    }

    private suspend fun sendTaskCreatedNotification(supervisorId: String, task: Task) {
        // Implement FCM notification sending
        // This would use Firebase Cloud Messaging to send notification to supervisor
    }

    private suspend fun sendTaskCompletionNotification(supervisorId: String, task: Task) {
        // Implement FCM notification sending
        // This would use Firebase Cloud Messaging to send notification to supervisor
    }

    // Helper function to get application context (needs proper implementation)
    private fun getApplication(): android.app.Application {
        // This should be properly injected or handled
        throw NotImplementedError("Application context needs to be properly injected")
    }
}