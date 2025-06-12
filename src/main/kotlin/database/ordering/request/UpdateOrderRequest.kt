package com.example.database.ordering.request

import kotlinx.serialization.Serializable

@Serializable
data class UpdateOrderRequest(
    val weight: Double? = null,
    val totalPrice: Double? = null,
    val status: String
)