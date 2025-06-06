package com.example.database.recipient

data class RecipientDTO(
    val name: String,
    val surname: String,
    val patronymic: String?,
    val addressId: Int,
    val phone: String
)