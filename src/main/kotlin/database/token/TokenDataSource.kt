package com.example.database.token


interface TokenDataSource {
//    suspend fun getTokenById(id: Int): Token?
    suspend fun insertToken(token: Token) : Boolean
//    suspend fun tokenExists(id: Int) : Boolean
}