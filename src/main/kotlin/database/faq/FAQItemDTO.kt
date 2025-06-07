package com.example.database.faq

import kotlinx.serialization.Serializable

@Serializable
data class FAQItemDTO(
    val question:String,
    val answer: String
)

