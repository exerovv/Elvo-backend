package com.example.database.ordering.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class OrderStatusDTO(
    val orderId: Int,
    val statusId: Int,
    val createdAt: Instant
)
