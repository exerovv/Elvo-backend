package com.example.database.recipient

import kotlinx.serialization.Serializable

@Serializable
data class UpdateRecipientRequest(
    val name: String? = null,
    val surname: String? = null,
    val patronymic: String? = null,
    val city: String? = null,
    val street: String? = null,
    val house: Int? = null,
    val building: String? = null,
    val flat: Int? = null,
    val floor: Int? = null,
    val phone: String? = null
)