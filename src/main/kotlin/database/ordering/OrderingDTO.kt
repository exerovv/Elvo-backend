package com.example.database.ordering

import com.example.database.recipient.RecipientTable
import com.example.database.user.UserTable
import kotlinx.datetime.Instant

data class OrderingDTO(
    val recipientId: Int,
    val createdAt: Instant,
    val weight: Double,
    val totalPrice: Double,
    val currentStatusId: Int,
    val globalStatus: String
)

