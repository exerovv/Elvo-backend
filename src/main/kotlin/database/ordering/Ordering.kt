package com.example.database.ordering

import java.math.BigDecimal

data class Ordering(
    val userId: Int,
    val name: String,
    val address: String,
    val phone: String,
    val weight: BigDecimal,
    val deliveryPrice: BigDecimal,
    val totalPrice: BigDecimal
)