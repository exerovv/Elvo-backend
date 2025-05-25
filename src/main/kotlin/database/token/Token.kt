package com.example.database.token

data class Token(
    val id: Int,
    val userId: Int,
    val refreshToken: String,
    val revoked: Boolean
)
