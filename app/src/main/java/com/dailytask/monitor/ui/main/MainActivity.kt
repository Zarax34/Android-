package com.dailytask.monitor.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dailytask.monitor.data.model.User
import com.dailytask.monitor.ui.qr.QRCodeScreen
import com.dailytask.monitor.ui.tasks.TaskListScreen
import com.dailytask.monitor.ui.theme.DailyTaskMonitorTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val userType = intent.getStringExtra("userType")?.let { 
            User.UserType.valueOf(it) 
        } ?: User.UserType.USER
        
        setContent {
            DailyTaskMonitorTheme {
                MainScreen(userType = userType)
            }
        }
    }
}

@Composable
fun MainScreen(userType: User.UserType) {
    val navController = rememberNavController()
    
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController, userType = userType)
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "tasks",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("tasks") {
                TaskListScreen()
            }
            
            composable("qr") {
                QRCodeScreen()
            }
            
            composable("settings") {
                SettingsScreen()
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    navController: androidx.navigation.NavHostController,
    userType: User.UserType
) {
    var selectedItem by remember { mutableStateOf(0) }
    
    val items = if (userType == User.UserType.USER) {
        listOf(
            BottomNavItem("tasks", "المهام", androidx.compose.material.icons.Icons.Default.List),
            BottomNavItem("qr", "QR Code", androidx.compose.material.icons.Icons.Default.QrCode),
            BottomNavItem("settings", "الإعدادات", androidx.compose.material.icons.Icons.Default.Settings)
        )
    } else {
        listOf(
            BottomNavItem("tasks", "المهام", androidx.compose.material.icons.Icons.Default.List),
            BottomNavItem("settings", "الإعدادات", androidx.compose.material.icons.Icons.Default.Settings)
        )
    }

    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = selectedItem == index,
                onClick = {
                    selectedItem = index
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)