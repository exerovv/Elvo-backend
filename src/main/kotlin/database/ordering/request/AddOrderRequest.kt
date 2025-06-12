package com.example.database.ordering.request

import kotlinx.serialization.Serializable

@Serializable
data class AddOrderRequest(
    val recipientId: Int?,
    val orderName: String?,
    val trackNumber: String?,
    val ruDescription: String?,
    val chDescription: String?,
    val link: String?,
    val itemPrice: Double?
)