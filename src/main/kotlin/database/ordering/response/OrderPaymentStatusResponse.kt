package com.example.database.ordering.response

import kotlinx.serialization.Serializable

@Serializable
data class OrderPaymentStatusResponse(
    val orderId: Int,
    val paymentStatus: String
)