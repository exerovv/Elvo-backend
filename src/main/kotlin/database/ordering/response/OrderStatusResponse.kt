package com.example.database.ordering.response

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrderStatusResponse(
    val name: String,
    val icon: String,
    @SerialName("created_at")
    val createdAt: Instant
)