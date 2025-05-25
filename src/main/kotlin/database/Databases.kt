package com.example.database

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.*

fun Application.configureDatabases() {
    val postgres = environment.config.config("ktor-postgres")
    Database.connect(
        url = postgres.property("url").getString(),
        user = postgres.property("user").getString(),
        driver = "org.postgresql.Driver",
        password = postgres.property("password").getString()
    )
}
