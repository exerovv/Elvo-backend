package com.example.database.ordering

import kotlinx.datetime.LocalDateTime
import java.math.BigDecimal

data class OrderingDTO(
    val orderingId: Int,
    val userId: Int,
    val status: String,
    val formationDate: LocalDateTime,
    val weight: BigDecimal,
    val deliveryPrice: BigDecimal,
    val totalPrice: BigDecimal
)

