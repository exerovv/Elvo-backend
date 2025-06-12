package com.example.database.ordering.response

data class OrderPaymentStatusResponse(
    val orderId: Int,
    val paymentStatus: String
)