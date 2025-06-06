package com.example.database.address

import kotlinx.serialization.Serializable

@Serializable
data class AddressDTO(
    val city: String,
    val street: String,
    val house: Int,
    val building: String?,
    val flat: Int,
    val floor: Int
)