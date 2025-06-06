package com.example.database.recipient

import kotlinx.serialization.Serializable

@Serializable
data class RecipientDTO(
    val name: String,
    val surname: String,
    val patronymic: String?,
    val addressId: Int,
    val phone: String
)