package com.example.security.utils

import kotlinx.serialization.Serializable

@Serializable
enum class ErrorCode {
    INCORRECT_CREDENTIALS,
    USER_NOT_FOUND,
    BLANK_CREDENTIALS,
    SHORT_PASSWORD,
    USER_ALREADY_EXISTS,
    SERVER_ERROR,
    SESSION_EXPIRED,
    CHECK_CREDENTIALS
}