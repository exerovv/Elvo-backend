package com.example.database

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.sql.Connection
import java.sql.DriverManager
import org.jetbrains.exposed.sql.*

fun Application.configureDatabases() {
    Database.connect(
        url = "jdbc:postgresql://localhost:5432/elvo",
        user = "root",
        driver = "org.postgresql.Driver",
        password = "1234")



}
