package com.example.database.ordering.response

import com.example.database.recipient.SingleRecipientResponse

data class OrderFullInfoResponse(
    val order: OrderFullResponse,
    val recipient: SingleRecipientResponse
)