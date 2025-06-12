package com.example.database.recipient

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SingleRecipientResponse(
    val name: String,
    val surname: String,
    val patronymic: String,
    @SerialName("full_name")
    val fullName: String,
    val phone: String,
    val city: String,
    val street: String,
    val house: Int,
    val building: String,
    val flat: Int,
    val floor: Int,
    val address: String
)
