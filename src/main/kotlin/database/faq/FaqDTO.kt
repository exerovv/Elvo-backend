package com.example.database.faq

import kotlinx.serialization.Serializable

@Serializable
data class FaqDTO(
    val question:String,
    val answer: String
)

