package com.example.database.models

interface UserDataSource {
    suspend fun getUserByUsername(username: String): User?
    suspend fun insertUser(user: User) : Boolean
    suspend fun userExists(username: String) : Boolean

}