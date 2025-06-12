package com.example.database.ordering.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrderShortResponse(
    @SerialName("recipient_id")
    val orderingId: Int,
    @SerialName("order_name")
    val orderName: String,
    @SerialName("current_status")
    val currentStatus: String,
    @SerialName("global_status")
    val globalStatus: String,
    @SerialName("payment_status")
    val paymentStatus: String
)
