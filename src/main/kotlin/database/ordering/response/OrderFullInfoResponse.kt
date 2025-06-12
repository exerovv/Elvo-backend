package com.example.database.ordering.response

import com.example.database.recipient.SingleRecipientResponse
import kotlinx.serialization.Serializable

@Serializable
data class OrderFullInfoResponse(
    val order: OrderFullResponse,
    val recipient: SingleRecipientResponse
)