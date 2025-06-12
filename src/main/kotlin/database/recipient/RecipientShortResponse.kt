package com.example.database.recipient

import kotlinx.serialization.Serializable

@Serializable
data class RecipientShortResponse(
    val recipientId: Int,
    val fullName: String
)