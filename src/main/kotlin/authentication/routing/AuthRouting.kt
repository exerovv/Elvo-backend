package com.example.authentication.routing

import com.example.authentication.JWTConfig
import com.example.authentication.generateToken
import com.example.authentication.model.AuthRequest
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.util.caseInsensitiveMap

public fun Application.authRouting(config : JWTConfig){
    routing {
        post("signup"){
            val requestData = call.receive<AuthRequest>()
            if (TODO("Таблица юзеров содержит такой никнейм")){
                call.respondText("User already exist")
            }else{
                TODO("Вставить пользователя в таблицу")
                val token = generateToken(config = config, username = requestData.username)
                call.respond(mapOf("token" to token))
            }
        }
        post("signin"){
            val requestData = call.receive<AuthRequest>()
            val storedPassword = TODO("Либо находим пароль по имени пользователя, либо return@post с текстом, что пользователя не сущ")
            if (storedPassword == requestData.password){
                val token = generateToken(config = config, username = requestData.username)
                call.respond(mapOf("token" to token))
            }else{
                call.respondText("Invalid credentials")
            }
        }

        authenticate("jwt-auth"){
            get(""){
                val principal = call.principal<JWTPrincipal>()
                val username = principal!!.payload.getClaim("username").asString()
                val expiresAt = principal.expiresAt?.time?.minus(System.currentTimeMillis())
                call.respondText("Hello, $username! The token expires after $expiresAt ms")
            }
        }
    }
}