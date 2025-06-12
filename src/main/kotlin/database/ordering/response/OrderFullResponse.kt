package com.example.database.ordering.response

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrderFullResponse(
    @SerialName("recipient_id")
    val recipientId: Int,
    @SerialName("order_name")
    val orderName: String,
    @SerialName("track_number")
    val trackNumber: String,
    @SerialName("created_at")
    val createdAt: Instant,
    val weight: Double?,
    @SerialName("total_price")
    val totalPrice: Double?,
    @SerialName("current_status")
    val currentStatus: String,
    @SerialName("global_status")
    val globalStatus: String,
    @SerialName("payment_status")
    val paymentStatus: String,
    @SerialName("ru_description")
    val ruDescription: String,
    @SerialName("ch_description")
    val chDescription: String,
    val link: String,
    @SerialName("item_price")
    val itemPrice: Double
)