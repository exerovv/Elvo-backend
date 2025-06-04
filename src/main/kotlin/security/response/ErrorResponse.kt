package com.example.security.response

import com.example.security.utils.ErrorCode
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    @SerialName("error_code")
    val errorCode: ErrorCode
)