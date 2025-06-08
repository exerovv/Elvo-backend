package com.example.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.core.ErrorResponse
import com.example.security.token.JWTTokenConfig
import com.example.utils.ErrorCode
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*

fun Application.configureSecurity(jwtTokenConfig: JWTTokenConfig) {
    install(Authentication){
        jwt("jwt-auth"){
            realm = this@configureSecurity.environment.config.property("ktor.jwt.realm").getString()
            val jwtVerifier = JWT
                .require(Algorithm.HMAC256(jwtTokenConfig.secret))
                .withAudience(jwtTokenConfig.audience)
                .withIssuer(jwtTokenConfig.issuer)
                .build()

            verifier(jwtVerifier)

            validate { jwtCredential ->
                println("Claims: ${jwtCredential.payload.claims.mapValues { it.value.asString() }}")
                val userId = jwtCredential.payload.getClaim("user_id").asInt()
                if (userId != null){
                    JWTPrincipal(jwtCredential.payload)
                }else{
                    null
                }
            }

            challenge { _, _ ->
                println("Auth failed: token = ${call.request.headers["Authorization"]}")
                call.respond(
                    HttpStatusCode.BadRequest, ErrorResponse(
                        errorCode = ErrorCode.UNAUTHORIZED
                    )
                )
            }
        }
    }
}
