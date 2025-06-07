package com.example.database.recipient

import kotlinx.serialization.Serializable

@Serializable
data class SingleRecipientResponse(
    val fullName: String,
    val phone: String,
    val address: String
)
