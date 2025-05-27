package com.example.security.response

import com.example.security.utils.ErrorCode

data class ApiResponse<T>(
    val success: Boolean,
    val errorCode: ErrorCode? = null,
    val data: T? = null
)
