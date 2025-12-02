package com.dailytask.monitor.ui.qr

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dailytask.monitor.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QRCodeViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _userId = MutableStateFlow("")
    val userId: StateFlow<String> = _userId.asStateFlow()

    init {
        loadCurrentUserId()
    }

    private fun loadCurrentUserId() {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            _userId.value = user?.userId ?: ""
        }
    }

    fun generateNewUserId() {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            user?.let {
                _userId.value = it.userId // Generate new ID if needed
            }
        }
    }
}