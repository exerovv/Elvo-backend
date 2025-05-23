package com.example

import com.example.authentication.JWTConfig
import com.example.authentication.configureSecurity
import com.example.database.configureDatabases
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val jwt = environment.config.config("ktor.jwt")
    val config = JWTConfig(
        realm = jwt.property("realm").getString(),
        secret = jwt.property("secret").getString(),
        issuer = jwt.property("issuer").getString(),
        audience = jwt.property("audience").getString(),
        tokenExpiry = jwt.property("expiry").getString().toLong()
    )
    configureSerialization()
    configureDatabases()
    configureSecurity(config)
    configureRouting()
}
