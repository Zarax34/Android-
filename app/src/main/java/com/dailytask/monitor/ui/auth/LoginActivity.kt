package com.dailytask.monitor.ui.auth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dailytask.monitor.ui.theme.DailyTaskMonitorTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DailyTaskMonitorTheme {
                LoginScreen(
                    onLoginSuccess = {
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun LoginScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoginMode by remember { mutableStateOf(true) }
    var userType by remember { mutableStateOf(com.dailytask.monitor.data.model.User.UserType.USER) }
    
    val authState by viewModel.authState.collectAsState()
    
    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onLoginSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (isLoginMode) "تسجيل الدخول" else "إنشاء حساب",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // User type selection (only for registration)
        if (!isLoginMode) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FilterChip(
                    selected = userType == com.dailytask.monitor.data.model.User.UserType.USER,
                    onClick = { userType = com.dailytask.monitor.data.model.User.UserType.USER },
                    label = { Text("مستخدم") }
                )
                FilterChip(
                    selected = userType == com.dailytask.monitor.data.model.User.UserType.SUPERVISOR,
                    onClick = { userType = com.dailytask.monitor.data.model.User.UserType.SUPERVISOR },
                    label = { Text("مراقب") }
                )
            }
        }

        // Email field
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("البريد الإلكتروني") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true
        )

        // Password field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("كلمة المرور") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            singleLine = true
        )

        // Error message
        if (authState is AuthState.Error) {
            Text(
                text = (authState as AuthState.Error).message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Loading state
        if (authState is AuthState.Loading) {
            CircularProgressIndicator(
                modifier = Modifier.padding(bottom = 16.dp)
            )
        } else {
            // Action button
            Button(
                onClick = {
                    if (isLoginMode) {
                        viewModel.login(email, password)
                    } else {
                        viewModel.register(email, password, userType)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text(text = if (isLoginMode) "تسجيل الدخول" else "إنشاء حساب")
            }
        }

        // Toggle between login and register
        TextButton(
            onClick = { isLoginMode = !isLoginMode }
        ) {
            Text(
                text = if (isLoginMode) "ليس لديك حساب؟ إنشاء حساب" 
                      else "لديك حساب بالفعل؟ تسجيل الدخول"
            )
        }
    }
}