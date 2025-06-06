package com.example.database.token

import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal

fun ApplicationCall.getUserIdClaim(): Int? {
    val principal = principal<JWTPrincipal>()
    return principal?.getClaim("user_id", Int::class)
}