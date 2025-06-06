package com.example.database.address

data class AddressDTO(
    val id: Int,
    val city: String,
    val street: String,
    val house: Int,
    val building: Int,
    val flat: Int,
    val floor: Int
)