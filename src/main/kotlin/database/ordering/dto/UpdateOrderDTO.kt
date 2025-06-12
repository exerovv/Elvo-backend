package com.example.database.ordering.dto

data class UpdateOrderDTO(
    val weight: Double? = null,
    val totalPrice: Double? = null,
    val updateStatusId: Int,
    val paymentStatus: String? = null
)
