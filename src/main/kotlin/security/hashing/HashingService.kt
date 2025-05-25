package com.example.security.hashing

interface HashingService {
    fun generateSaltedHash(value : String) : String
    fun verify(value: String, hash: String) : Boolean
}