package com.example.database.ordering.response

import kotlinx.serialization.Serializable

@Serializable
data class OrderShortResponse(
    val orderingId: Int,
    val orderName: String,
    val status: String,
    val globalStatus: String,
    val isPaid: String
)
