package com.example.database.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserInfoResponse(
    @SerialName("user_id")
    val userId: Int,
    @SerialName("username")
    val username: String,
    @SerialName("avatar_url")
    val avatarUrl: String
)
