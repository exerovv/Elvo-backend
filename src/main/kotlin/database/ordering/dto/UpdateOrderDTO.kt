package com.example.database.ordering.dto

import kotlinx.serialization.Serializable

@Serializable
data class UpdateOrderDTO(
    val weight: Double? = null,
    val totalPrice: Double? = null,
    val updateStatusId: Int,
    val paymentStatus: String? = null
)
