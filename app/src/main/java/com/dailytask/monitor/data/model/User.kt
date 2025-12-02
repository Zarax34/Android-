package com.dailytask.monitor.data.model

data class User(
    val userId: String = "",
    val email: String = "",
    val userType: UserType = UserType.USER,
    val startTime: String = "06:00",
    val linkedSupervisorId: String? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    enum class UserType {
        USER, SUPERVISOR
    }
}

data class Task(
    val taskId: String = "",
    val userId: String = "",
    val title: String = "",
    val description: String = "",
    val time: String = "",
    val urgencyLevel: UrgencyLevel = UrgencyLevel.MEDIUM,
    val status: TaskStatus = TaskStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val confirmedBySupervisor: Boolean = false,
    val messageToSupervisor: String = ""
) {
    enum class UrgencyLevel {
        LOW, MEDIUM, HIGH, CRITICAL
    }
    
    enum class TaskStatus {
        PENDING, IN_PROGRESS, COMPLETED, CONFIRMED
    }
}

data class SupervisorLink(
    val supervisorId: String = "",
    val userId: String = "",
    val linkedAt: Long = System.currentTimeMillis()
)