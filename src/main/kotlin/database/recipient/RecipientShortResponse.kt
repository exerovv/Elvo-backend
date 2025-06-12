package com.example.database.recipient

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecipientShortResponse(
    @SerialName("recipient_id")
    val recipientId: Int,
    @SerialName("full_name")
    val fullName: String
)