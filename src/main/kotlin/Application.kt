package com.example

import com.example.authentication.configureSecurity
import com.example.database.configureDatabases
import com.example.database.user.UserDataSourceImpl
import com.example.security.hashing.SHA256HashingService
import com.example.security.token.JwtTokenService
import com.example.security.token.TokenConfig
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val jwt = environment.config.config("ktor.jwt")
    val tokenService = JwtTokenService()
    val tokenConfig = TokenConfig(
        issuer = jwt.property("issuer").getString(),
        audience = jwt.property("audience").getString(),
        expiresIn = jwt.property("expiry").getString().toLong(),
        secret = jwt.property("secret").getString(),
    )
    val hashingService = SHA256HashingService()
    val userDataSource = UserDataSourceImpl()
    configureSerialization()
    configureDatabases()
    configureSecurity(tokenConfig)
    configureRouting(userDataSource, hashingService, tokenService, tokenConfig)
}
