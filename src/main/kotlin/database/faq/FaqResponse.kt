package com.example.database.faq

import kotlinx.serialization.Serializable

@Serializable
data class FaqResponse(
    val question:String,
    val answer: String
)