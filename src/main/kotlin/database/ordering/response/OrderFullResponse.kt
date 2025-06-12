package com.example.database.ordering.response

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class OrderFullResponse(
    val recipientId: Int,
    val orderName: String,
    val trackNumber: String,
    val createdAt: Instant,
    val weight: Double?,
    val totalPrice: Double?,
    val currentStatus: String,
    val globalStatus: String,
    val isPaid: String,
    val ruDescription: String,
    val chDescription: String,
    val link: String,
    val itemPrice: Double
)