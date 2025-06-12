package com.example.database.recipient

import kotlinx.serialization.Serializable

@Serializable
data class UpdateResponse(
    val updatedRecipient: RecipientShortResponse? = null
)
