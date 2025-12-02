package com.dailytask.monitor.data.repository

import com.dailytask.monitor.data.model.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(
    private val database: FirebaseDatabase
) {

    suspend fun createTask(task: Task): Result<Unit> {
        return try {
            database.reference
                .child("tasks")
                .child(task.userId)
                .child(task.taskId)
                .setValue(task)
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateTask(task: Task): Result<Unit> {
        return try {
            database.reference
                .child("tasks")
                .child(task.userId)
                .child(task.taskId)
                .setValue(task)
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteTask(userId: String, taskId: String): Result<Unit> {
        return try {
            database.reference
                .child("tasks")
                .child(userId)
                .child(taskId)
                .removeValue()
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getTasksFlow(userId: String): Flow<List<Task>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tasks = snapshot.children.mapNotNull { child ->
                    child.getValue(Task::class.java)
                }
                trySend(tasks)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        val reference = database.reference
            .child("tasks")
            .child(userId)

        reference.addValueEventListener(listener)

        awaitClose {
            reference.removeEventListener(listener)
        }
    }

    suspend fun getTask(userId: String, taskId: String): Task? {
        return try {
            val snapshot = database.reference
                .child("tasks")
                .child(userId)
                .child(taskId)
                .get()
                .await()
            
            snapshot.getValue(Task::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun linkSupervisor(userId: String, supervisorId: String): Result<Unit> {
        return try {
            // Update user's linked supervisor
            database.reference
                .child("users")
                .child(userId)
                .child("linkedSupervisorId")
                .setValue(supervisorId)
                .await()

            // Create supervisor link record
            val supervisorLink = mapOf(
                "supervisorId" to supervisorId,
                "userId" to userId,
                "linkedAt" to System.currentTimeMillis()
            )

            database.reference
                .child("supervisor_links")
                .child(supervisorId)
                .child(userId)
                .setValue(supervisorLink)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getTasksForSupervisor(supervisorId: String): Flow<List<Task>> = callbackFlow {
        // First get linked users
        val usersListener = object : ValueEventListener {
            override fun onDataChange(usersSnapshot: DataSnapshot) {
                val linkedUserIds = usersSnapshot.children.map { it.child("userId").value as String }
                
                // Then get tasks for each linked user
                val allTasks = mutableListOf<Task>()
                var pendingLoads = linkedUserIds.size

                if (pendingLoads == 0) {
                    trySend(emptyList())
                    return
                }

                for (userId in linkedUserIds) {
                    database.reference
                        .child("tasks")
                        .child(userId)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(tasksSnapshot: DataSnapshot) {
                                val tasks = tasksSnapshot.children.mapNotNull { child ->
                                    child.getValue(Task::class.java)
                                }
                                allTasks.addAll(tasks)
                                pendingLoads--
                                
                                if (pendingLoads == 0) {
                                    trySend(allTasks)
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                pendingLoads--
                                if (pendingLoads == 0) {
                                    trySend(allTasks)
                                }
                            }
                        })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        database.reference
            .child("supervisor_links")
            .child(supervisorId)
            .addValueEventListener(usersListener)

        awaitClose {
            database.reference
                .child("supervisor_links")
                .child(supervisorId)
                .removeEventListener(usersListener)
        }
    }
}