package com.example

import com.example.authentication.JWTConfig
import com.example.authentication.routing.authRouting
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    val jwt = environment.config.config("ktor.jwt")
    val config = JWTConfig(
        realm = jwt.property("realm").getString(),
        secret = jwt.property("secret").getString(),
        issuer = jwt.property("issuer").getString(),
        audience = jwt.property("audience").getString(),
        tokenExpiry = jwt.property("expiry").getString().toLong()
    )
    authRouting(config)
}
