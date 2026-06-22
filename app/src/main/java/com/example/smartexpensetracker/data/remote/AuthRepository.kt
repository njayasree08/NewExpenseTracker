package com.example.smartexpensetracker.data.remote

import com.example.smartexpensetracker.data.models.User

interface AuthRepository {
    val currentUser: User?

    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(email: String, password: String): Result<User>
    suspend fun logout()
}
