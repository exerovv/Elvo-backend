package com.example.database.recipient

import kotlinx.serialization.Serializable

@Serializable
data class RecipientShortDTO(
    val recipientId: Int,
    val name: String
)