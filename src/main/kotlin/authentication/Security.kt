package com.example.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.respondText

fun Application.configureSecurity(config : JWTConfig) {
    install(Authentication){
        jwt("jwt-auth"){
            realm = config.realm
            val jwtVerifier = JWT
                .require(Algorithm.HMAC256(config.secret))
                .withAudience(config.audience)
                .withIssuer(config.issuer)
                .build()
            verifier(jwtVerifier)

            validate { jwtCredential ->
                val username = jwtCredential.payload.getClaim("username").asString()
                if (!username.isNullOrBlank()){
                    JWTPrincipal(jwtCredential.payload)
                }else{
                    null
                }
            }

            challenge { _, _ ->
                call.respondText("Token is not valid or has expired",
                    status = HttpStatusCode.Unauthorized)
            }
        }
    }
}

data class JWTConfig(
    val realm : String,
    val secret : String,
    val issuer : String,
    val audience : String,
    val tokenExpiry : Long,
)
