package com.example.database.token

data class Token(
    val userId: Int,
    val refreshToken: String,
    val expiration : Long,
    val revoked: Boolean
)
