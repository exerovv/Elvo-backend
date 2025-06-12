package com.example.database.ordering.dto

import kotlinx.serialization.Serializable

@Serializable
data class StatusDTO(
    val id: Int,
    val name: String,
    val globalStatus: String
)
