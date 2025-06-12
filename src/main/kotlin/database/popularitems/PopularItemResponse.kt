package com.example.database.popularitems

import kotlinx.serialization.Serializable

@Serializable
data class PopularItemResponse(
    val popularItemId: Int,
    val title:String,
    val url: String
)

