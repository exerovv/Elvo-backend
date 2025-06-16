package com.example.database.ordering.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaymentStatusResponse(
    @SerialName("payment_status")
    val paymentStatus: String
)