package com.example

import com.example.database.user.UserDataSource
import com.example.security.hashing.HashingService
import com.example.security.routing.authRouting
import com.example.security.token.TokenConfig
import com.example.security.token.TokenService
import io.ktor.server.application.*

fun Application.configureRouting(
    userDataSource: UserDataSource,
    hashingService: HashingService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    authRouting(hashingService, userDataSource, tokenService, tokenConfig)
}
