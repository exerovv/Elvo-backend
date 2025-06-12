package com.example.database.ordering.response

import kotlinx.datetime.Instant

data class OrderStatusResponse(
    val name: String,
    val icon: String,
    val createdAt: Instant
)