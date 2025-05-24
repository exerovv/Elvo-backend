package com.example.database.models

data class User (
    val username: String,
    val password: String,
    val salt: String
    )