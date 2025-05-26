package com.example.security.routing

import com.example.database.token.Token
import com.example.database.token.TokenDataSource
import com.example.security.request.AuthRequest
import com.example.database.user.*
import com.example.security.hashing.HashingService
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
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.UUID

fun Application.authRouting(
    hashingService: HashingService,
    userDataSource: UserDataSource,
    tokenService: TokenService,
    tokenConfig: TokenConfig,
    tokenDataSource: TokenDataSource
) {
//    routing {
//        post("signup") {
//            val requestData = call.receiveOrNull<AuthRequest>() ?: run {
//                call.respond(HttpStatusCode.BadRequest, "Incorrect credentials")
//                return@post
//            }
//
//            val areFieldsBlank = requestData.username.isBlank() || requestData.password.isBlank()
//            val isPwdTooShort = requestData.password.length < 8
//            val userExists = userDataSource.userExists(requestData.username)
//            if (areFieldsBlank) {
//                call.respond(HttpStatusCode.Conflict, "Credentials cant be blank")
//                return@post
//            }
//            if (isPwdTooShort) {
//                call.respond(HttpStatusCode.Conflict, "Password is too short")
//                return@post
//
//            }
//            if (userExists) {
//                call.respond(HttpStatusCode.Conflict, "User already exists")
//                return@post
//            } else {
//                val hash = hashingService.generateSaltedHash(requestData.password)
//                val user = User(
//                    username = requestData.username,
//                    password = hash
//                )
//
//                TODO("Изменить способ создания рефреш токена")
//                val refreshToken = UUID.randomUUID().toString()
//
//                val userId = newSuspendedTransaction {
//                    val userId = userDataSource.insertUser(user)
//                    userId?.let{
//                        tokenDataSource.insertToken(Token(userId, refreshToken, 900000, false))
//                    }
//                    userId
//                }
//                if (userId == null) {
//                    call.respond(HttpStatusCode.Conflict, "Error on the server side")
//                    return@post
//                }
//                val accessToken = tokenService.generate(
//                    config = tokenConfig,
//                    TokenClaim(
//                        name = "username",
//                        value = user.username
//                    ),
//                    TokenClaim(
//                        name = "user_id",
//                        value = userId
//                    )
//                )
//                call.respond(
//                    status = HttpStatusCode.OK,
//                    message = AuthResponse(accessToken)
//                )
//            }
//        }
//        post("signin") {
//            val requestData = call.receiveOrNull<AuthRequest>() ?: run {
//                call.respond(HttpStatusCode.BadRequest, "Incorrect credentials")
//                return@post
//            }
//            val foundUser = userDataSource.getUserByUsername(requestData.username)
//            if (foundUser == null) {
//                call.respond(HttpStatusCode.Conflict, "User with this credentials doesnt exist")
//                return@post
//            }
//            val isValidPassword = hashingService.verify(
//                value = requestData.password,
//                hash =  foundUser.user.password
//            )
//
//            if (!isValidPassword) {
//                call.respond(HttpStatusCode.BadRequest, "Incorrect credentials")
//                return@post
//            }
//
//            val token = tokenService.generate(
//                config = tokenConfig,
//                TokenClaim(
//                    name = "username",
//                    value = foundUser.user.password
//                )
//            )
//
//            call.respond(
//                status = HttpStatusCode.OK,
//                message = AuthResponse(token)
//            )
//        }
//
//        authenticate("jwt-auth") {
//            get("authenticate") {
//                call.respond(HttpStatusCode.OK)
//            }
//        }
//
//        authenticate {
//            get("secret"){
//                val principal = call.principal<JWTPrincipal>()
//                val username = principal?.getClaim("username", String::class)
//                call.respond(HttpStatusCode.OK, "Your username is $username")
//            }
//        }
//    }
}