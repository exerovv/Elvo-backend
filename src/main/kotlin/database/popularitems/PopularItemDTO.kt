package com.example.database.popularitems

import kotlinx.serialization.Serializable

@Serializable
data class PopularItemDTO(
    val popularItemId: Int,
    val title:String,
    val url: String
)

