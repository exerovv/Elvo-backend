package com.example.database.address

data class AddressDTO(
    val city: String,
    val street: String,
    val house: Int,
    val building: String?,
    val flat: Int,
    val floor: Int
)