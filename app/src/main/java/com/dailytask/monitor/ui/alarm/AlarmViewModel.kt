package com.dailytask.monitor.ui.alarm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dailytask.monitor.data.model.Task
import com.dailytask.monitor.data.repository.AuthRepository
import com.dailytask.monitor.data.repository.TaskRepository
import com.dailytask.monitor.service.AlarmService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    application: Application,
    private val taskRepository: TaskRepository,
    private val authRepository: AuthRepository
) : AndroidViewModel(application) {

    private val _confirmationState = MutableStateFlow<ConfirmationState>(ConfirmationState.WaitingForUser)
    val confirmationState: StateFlow<ConfirmationState> = _confirmationState.asStateFlow()
    
    private var taskId: String = ""
    private var userId: String = ""
    private var currentTask: Task? = null

    fun initialize(taskId: String, userId: String) {
        this.taskId = taskId
        this.userId = userId
        loadTask()
    }

    private fun loadTask() {
        viewModelScope.launch {
            val task = taskRepository.getTask(userId, taskId)
            currentTask = task
            
            if (task?.status == Task.TaskStatus.COMPLETED) {
                _confirmationState.value = ConfirmationState.WaitingForSupervisor
            }
        }
    }

    fun markTaskCompleted() {
        viewModelScope.launch {
            val task = currentTask ?: return@launch
            
            // Update task status to completed
            val updatedTask = task.copy(
                status = Task.TaskStatus.COMPLETED,
                completedAt = System.currentTimeMillis()
            )
            
            val result = taskRepository.updateTask(updatedTask)
            if (result.isSuccess) {
                currentTask = updatedTask
                _confirmationState.value = ConfirmationState.WaitingForSupervisor
                
                // Send notification to supervisor
                sendCompletionNotificationToSupervisor(updatedTask)
            }
        }
    }

    private suspend fun sendCompletionNotificationToSupervisor(task: Task) {
        val currentUser = authRepository.getCurrentUser()
        val supervisorId = currentUser?.linkedSupervisorId
        
        if (supervisorId != null) {
            // Send FCM notification to supervisor
            // Implementation would include Firebase Cloud Messaging
            // For now, we'll simulate the process
            
            // Simulate network delay
            kotlinx.coroutines.delay(2000)
            
            // For demo purposes, let's assume supervisor confirms after 2 seconds
            confirmTaskCompletion()
        }
    }

    private fun confirmTaskCompletion() {
        viewModelScope.launch {
            val task = currentTask ?: return@launch
            
            val confirmedTask = task.copy(
                status = Task.TaskStatus.CONFIRMED,
                confirmedBySupervisor = true
            )
            
            val result = taskRepository.updateTask(confirmedTask)
            if (result.isSuccess) {
                _confirmationState.value = ConfirmationState.Confirmed
                
                // Stop the alarm service
                stopAlarmService()
            }
        }
    }

    fun rejectTaskCompletion() {
        viewModelScope.launch {
            val task = currentTask ?: return@launch
            
            // Reset task to pending status
            val resetTask = task.copy(
                status = Task.TaskStatus.PENDING,
                completedAt = null
            )
            
            taskRepository.updateTask(resetTask)
            _confirmationState.value = ConfirmationState.Rejected
        }
    }

    private fun stopAlarmService() {
        val intent = Intent(getApplication(), AlarmService::class.java)
        getApplication<android.app.Application>().stopService(intent)
    }

    override fun onCleared() {
        super.onCleared()
        stopAlarmService()
    }
}

sealed class ConfirmationState {
    object WaitingForUser : ConfirmationState()
    object WaitingForSupervisor : ConfirmationState()
    object Confirmed : ConfirmationState()
    object Rejected : ConfirmationState()
}