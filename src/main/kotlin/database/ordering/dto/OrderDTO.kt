package com.example.database.ordering.dto

import kotlinx.datetime.Instant

data class OrderDTO(
    val recipientId: Int,
    val orderName: String,
    val trackNumber: String,
    val createdAt: Instant,
    val currentStatusId: Int,
    val globalStatus: String,
    val isPaid: String,
    val ruDescription: String,
    val chDescription: String,
    val link: String,
    val itemPrice: Double
)