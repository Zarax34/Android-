package com.dailytask.monitor.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dailytask.monitor.data.repository.AuthRepository
import com.dailytask.monitor.data.repository.TaskRepository
import com.dailytask.monitor.utils.AlarmScheduler
import com.dailytask.monitor.utils.ContextProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val taskRepository: TaskRepository,
    private val contextProvider: ContextProvider
) : ViewModel() {

    private val _startTime = MutableStateFlow("06:00")
    val startTime: StateFlow<String> = _startTime.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadCurrentSettings()
    }

    private fun loadCurrentSettings() {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            user?.let {
                _startTime.value = it.startTime
            }
        }
    }

    fun updateStartTime(time: String) {
        _startTime.value = time
    }

    fun saveSettings() {
        viewModelScope.launch {
            _isLoading.value = true
            
            val user = authRepository.getCurrentUser()
            if (user != null) {
                val updatedUser = user.copy(startTime = _startTime.value)
                
                // Update user in database
                // Note: This would require adding an update method to AuthRepository
                
                // Schedule daily alarm
                AlarmScheduler.scheduleDailyAlarm(
                    contextProvider.getApplication(),
                    user.userId,
                    _startTime.value
                )
            }
            
            _isLoading.value = false
        }
    }
}