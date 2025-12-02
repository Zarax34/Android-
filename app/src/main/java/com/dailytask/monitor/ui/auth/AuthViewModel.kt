package com.dailytask.monitor.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dailytask.monitor.data.model.User
import com.dailytask.monitor.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Please fill in all fields")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            
            val result = authRepository.loginUser(email, password)
            _authState.value = if (result.isSuccess) {
                AuthState.Success(result.getOrNull()!!)
            } else {
                AuthState.Error(result.exceptionOrNull()?.message ?: "Login failed")
            }
        }
    }

    fun register(email: String, password: String, userType: User.UserType) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Please fill in all fields")
            return
        }

        if (password.length < 6) {
            _authState.value = AuthState.Error("Password must be at least 6 characters")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            
            val result = authRepository.registerUser(email, password, userType)
            _authState.value = if (result.isSuccess) {
                AuthState.Success(result.getOrNull()!!)
            } else {
                AuthState.Error(result.exceptionOrNull()?.message ?: "Registration failed")
            }
        }
    }

    fun checkCurrentUser() {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            _authState.value = if (user != null) {
                AuthState.Success(user)
            } else {
                AuthState.Idle
            }
        }
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}