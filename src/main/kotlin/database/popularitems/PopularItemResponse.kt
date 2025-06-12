package com.example.database.popularitems

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PopularItemResponse(
    @SerialName("popular_item_id")
    val popularItemId: Int,
    val title:String,
    val url: String
)

