package com.example.database.ordering.response

data class OrderShortResponse(
    val orderingId: Int,
    val orderName: String,
    val status: String,
    val globalStatus: String,
    val isPaid: String
)
