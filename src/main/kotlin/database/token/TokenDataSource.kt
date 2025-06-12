package com.example.database.token


interface TokenDataSource {
    suspend fun insertToken(token: Token) : Int
    suspend fun findToken(userId: Int) : Token?
    suspend fun updateToken(token: Token) : Boolean
}