package com.example.database.ordering.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrderPaymentStatusResponse(
    @SerialName("order_id")
    val orderId: Int,
    @SerialName("payment_status")
    val paymentStatus: String
)