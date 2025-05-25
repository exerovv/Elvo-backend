package com.example.database.user

interface UserDataSource {
    suspend fun getUserById(id: Int): User?
    suspend fun insertUser(user: User) : Boolean
    suspend fun userExists(id: Int) : Boolean
}