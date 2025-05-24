package com.example.security.routing

import com.example.authentication.model.AuthRequest
import com.example.database.models.User
import com.example.database.models.UserDataSource
import com.example.security.hashing.HashingService
import com.example.security.hashing.SaltedHash
import com.example.security.response.AuthResponse
import com.example.security.token.TokenClaim
import com.example.security.token.TokenConfig
import com.example.security.token.TokenService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receiveOrNull
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing

fun Application.authRouting(
    hashingService: HashingService,
    userDataSource: UserDataSource,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    routing {
        post("signup") {
            val requestData = call.receiveOrNull<AuthRequest>() ?: run {
                call.respond(HttpStatusCode.BadRequest, "Incorrect credentials")
                return@post
            }

            val areFieldsBlank = requestData.username.isBlank() || requestData.password.isBlank()
            val isPwdTooShort = requestData.password.length < 8
            val userExists = userDataSource.userExists(requestData.username)
            if (areFieldsBlank) {
                call.respond(HttpStatusCode.Conflict, "Credentials cant be blank")
                return@post
            }
            if (isPwdTooShort) {
                call.respond(HttpStatusCode.Conflict, "Password is too short")
                return@post

            }
            if (userExists) {
                call.respond(HttpStatusCode.Conflict, "User already exists")
                return@post
            } else {
                val saltedHash = hashingService.generateSaltedHash(requestData.password)
                val user = User(
                    username = requestData.username,
                    password = saltedHash.hash,
                    salt = saltedHash.salt
                )
                val wasAdded = userDataSource.insertUser(user)
                if (!wasAdded) {
                    call.respond(HttpStatusCode.Conflict, "Error on the server side")
                    return@post
                }
                val token = tokenService.generate(
                    config = tokenConfig,
                    TokenClaim(
                        name = "username",
                        value = user.username
                    )
                )
                call.respond(
                    status = HttpStatusCode.OK,
                    message = AuthResponse(token)
                )
            }
        }
        post("signin") {
            val requestData = call.receiveOrNull<AuthRequest>() ?: run {
                call.respond(HttpStatusCode.BadRequest, "Incorrect credentials")
                return@post
            }
            val user = userDataSource.getUserByUsername(requestData.username)
            if (user == null) {
                call.respond(HttpStatusCode.Conflict, "User with this credentials doesnt exist")
                return@post
            }
            val isValidPassword = hashingService.verify(
                value = requestData.password,
                saltedHash = SaltedHash(
                    hash = user.password,
                    salt = user.salt
                )
            )

            if (!isValidPassword) {
                call.respond(HttpStatusCode.BadRequest, "Incorrect credentials")
                return@post
            }

            val token = tokenService.generate(
                config = tokenConfig,
                TokenClaim(
                    name = "username",
                    value = user.username
                )
            )

            call.respond(
                status = HttpStatusCode.OK,
                message = AuthResponse(token)
            )
        }

        authenticate("jwt-auth") {
            get("authenticate") {
                call.respond(HttpStatusCode.OK)
            }
        }

        authenticate {
            get("secret"){
                val principal = call.principal<JWTPrincipal>()
                val username = principal?.getClaim("username", String::class)
                call.respond(HttpStatusCode.OK, "Your username is $username")
            }
        }
    }
}