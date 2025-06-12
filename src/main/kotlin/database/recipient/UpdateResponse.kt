package com.example.database.recipient

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateResponse(
    @SerialName("updated_recipient")
    val updatedRecipient: RecipientShortResponse? = null
)
