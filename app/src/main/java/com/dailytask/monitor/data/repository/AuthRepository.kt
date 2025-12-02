package com.dailytask.monitor.data.repository

import com.dailytask.monitor.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val database: FirebaseDatabase
) {

    suspend fun loginUser(email: String, password: String): Result<User> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user
            
            if (firebaseUser != null) {
                val userSnapshot = database.reference
                    .child("users")
                    .child(firebaseUser.uid)
                    .get()
                    .await()
                
                val user = userSnapshot.getValue(User::class.java)
                if (user != null) {
                    Result.success(user)
                } else {
                    Result.failure(Exception("User data not found"))
                }
            } else {
                Result.failure(Exception("Login failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registerUser(email: String, password: String, userType: User.UserType): Result<User> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user
            
            if (firebaseUser != null) {
                val user = User(
                    userId = firebaseUser.uid,
                    email = email,
                    userType = userType
                )
                
                // Save user to database
                database.reference
                    .child("users")
                    .child(firebaseUser.uid)
                    .setValue(user)
                    .await()
                
                Result.success(user)
            } else {
                Result.failure(Exception("Registration failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCurrentUser(): User? {
        val firebaseUser = firebaseAuth.currentUser ?: return null
        
        return try {
            val userSnapshot = database.reference
                .child("users")
                .child(firebaseUser.uid)
                .get()
                .await()
            
            userSnapshot.getValue(User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun logout() {
        firebaseAuth.signOut()
    }

    fun getCurrentUserId(): String? = firebaseAuth.currentUser?.uid
}