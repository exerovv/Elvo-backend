package com.example.database.recipient

import kotlinx.serialization.Serializable

@Serializable
data class RecipientRequest(
    val name: String,
    val surname: String,
    val patronymic: String?,
    val city: String,
    val street: String,
    val house: Int,
    val building: String?,
    val flat: Int,
    val floor: Int,
    val phone: String
)