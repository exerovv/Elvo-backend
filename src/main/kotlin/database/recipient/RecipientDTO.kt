package com.example.database.recipient

data class RecipientDTO(
    val recipientId: Int,
    val userId: Int,
    val name: String,
    val address: String,
    val phone: String
)