package com.example.security.routing

import com.example.core.ErrorResponse
import com.example.database.token.Token
import com.example.database.token.TokenDataSource
import com.example.database.token.getUserIdClaim
import com.example.database.user.User
import com.example.database.user.UserDataSource
import com.example.security.hashing.HashingService
import com.example.security.request.AuthRequest
import com.example.security.request.RefreshRequest
import com.example.security.response.AuthResponse
import com.example.security.token.*
import com.example.utils.ErrorCode
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.authenticate
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.Clock
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import kotlin.time.Duration.Companion.milliseconds

fun Application.authRouting(
    hashingService: HashingService,
    userDataSource: UserDataSource,
    jwtTokenService: JwtTokenService,
    jwtTokenConfig: JWTTokenConfig,
    refreshTokenConfig: RefreshTokenConfig,
    refreshTokenService: RefreshTokenService,
    tokenDataSource: TokenDataSource
) {
    routing {
        post("signup") {
            val requestData = runCatching<AuthRequest?> { call.receiveNullable<AuthRequest>() }.getOrNull() ?: run {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(
                        errorCode = ErrorCode.INCORRECT_CREDENTIALS
                    )
                )
                return@post
            }

            val areFieldsBlank = requestData.username.isBlank() || requestData.password.isBlank()
            val isPwdTooShort = requestData.password.length < 8
            if (areFieldsBlank) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(
                        errorCode = ErrorCode.BLANK_CREDENTIALS
                    )
                )
                return@post
            }
            if (isPwdTooShort) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(
                        errorCode = ErrorCode.SHORT_PASSWORD
                    )
                )
                return@post
            } else {
                val hash = hashingService.generateHash(requestData.password)
                val user = User(
                    username = requestData.username,
                    password = hash
                )

                val refreshToken = refreshTokenService.generate(refreshTokenConfig)

                val userInfo = try {
                    newSuspendedTransaction {
                        val userId = userDataSource.insertUser(user)
                        tokenDataSource.insertToken(
                            Token(
                                userId,
                                refreshToken,
                                Clock.System.now(),
                                Clock.System.now().plus(refreshTokenConfig.expiresIn.milliseconds),
                                false
                            )
                        )
                        userDataSource.getUserInfoById(userId)
                    }
                } catch (_: Exception) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse(
                            errorCode = ErrorCode.USER_ALREADY_EXISTS
                        )
                    )
                    return@post
                }

                if(userInfo == null){
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse(
                            errorCode = ErrorCode.SERVER_ERROR
                        )
                    )
                    return@post
                }

                val accessToken = jwtTokenService.generate(
                    config = jwtTokenConfig,
                    TokenClaim(
                        name = "user_id",
                        value = userInfo.userId
                    )
                )
                call.respond(
                    status = HttpStatusCode.OK,
                    message = AuthResponse(
                        accessToken = accessToken,
                        refreshToken = refreshToken,
                        userInfoResponse = userInfo
                    )
                )
            }
        }
        post("signin") {
            val requestData = runCatching<AuthRequest?> { call.receiveNullable<AuthRequest>() }.getOrNull() ?: run {
                call.respond(
                    HttpStatusCode.BadRequest, ErrorResponse(
                        errorCode = ErrorCode.INCORRECT_CREDENTIALS
                    )
                )
                return@post
            }
            val foundUser = userDataSource.getUserByUsername(requestData.username)
            if (foundUser == null) {
                call.respond(
                    HttpStatusCode.Unauthorized,
                    ErrorResponse(
                        errorCode = ErrorCode.USER_NOT_FOUND
                    )
                )
                return@post

            }

            val isValidPassword = hashingService.verify(
                value = requestData.password,
                hash = foundUser.user.password
            )

            if (!isValidPassword) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(
                        errorCode = ErrorCode.CHECK_CREDENTIALS
                    )
                )
                return@post
            }

            val userInfo = try{
                userDataSource.getUserInfoById(foundUser.userId)
            }catch (_: Exception){
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(
                        errorCode = ErrorCode.SERVER_ERROR
                    )
                )
                return@post
            }

            if(userInfo == null){
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(
                        errorCode = ErrorCode.SERVER_ERROR
                    )
                )
                return@post
            }

            val accessToken = jwtTokenService.generate(
                config = jwtTokenConfig,
                TokenClaim(
                    name = "user_id",
                    value = foundUser.userId
                )
            )

            val refreshToken = refreshTokenService.generate(refreshTokenConfig)

            val result = tokenDataSource.updateToken(
                    Token(
                        foundUser.userId,
                        refreshToken,
                        Clock.System.now(),
                        Clock.System.now().plus(refreshTokenConfig.expiresIn.milliseconds),
                        false
                    )
                )

            if (!result) {
                call.respond(
                    HttpStatusCode.Conflict,
                    ErrorResponse(
                        errorCode = ErrorCode.SERVER_ERROR
                    )
                )
                return@post
            }

            call.respond(
                status = HttpStatusCode.OK,
                message = AuthResponse(
                    accessToken = accessToken,
                    refreshToken = refreshToken,
                    userInfoResponse = userInfo
                )
            )
        }

        post("{id}/refresh") {
            val requestData =
                runCatching<RefreshRequest?> { call.receiveNullable<RefreshRequest>() }.getOrNull() ?: run {
                    call.respond(
                        HttpStatusCode.BadRequest, ErrorResponse(
                            errorCode = ErrorCode.INCORRECT_CREDENTIALS
                        )
                    )
                    return@post
                }


            if (requestData.refreshToken.isBlank()) {
                call.respond(
                    HttpStatusCode.BadRequest, ErrorResponse(
                        errorCode = ErrorCode.BLANK_CREDENTIALS
                    )
                )
                return@post
            }

            val userId = call.parameters["id"]?.toInt()

            if (userId == null) {
                call.respond(
                    HttpStatusCode.BadRequest, ErrorResponse(
                        errorCode = ErrorCode.INCORRECT_CREDENTIALS
                    )
                )
                return@post
            }

            val foundToken = tokenDataSource.findToken(userId)

            if (foundToken == null || foundToken.expiresAt < Clock.System.now()) {
                call.respond(
                    HttpStatusCode.Unauthorized, ErrorResponse(
                        errorCode = ErrorCode.UNAUTHORIZED
                    )
                )
                return@post

            } else {
                val newAccessToken = jwtTokenService.generate(
                    config = jwtTokenConfig,
                    TokenClaim(
                        name = "user_id",
                        value = foundToken.userId
                    )
                )

                val refreshToken = refreshTokenService.generate(refreshTokenConfig)

                val result = tokenDataSource.updateToken(
                    Token(
                        foundToken.userId,
                        refreshToken,
                        Clock.System.now(),
                        Clock.System.now().plus(refreshTokenConfig.expiresIn.milliseconds),
                        false
                    )
                )
                if (!result) {
                    call.respond(
                        HttpStatusCode.Conflict,
                        ErrorResponse(
                            errorCode = ErrorCode.SERVER_ERROR
                        )
                    )
                    return@post
                }
                call.respond(
                    status = HttpStatusCode.OK,
                    message = AuthResponse(
                        accessToken = newAccessToken,
                        refreshToken = refreshToken
                    )
                )
            }
        }

        authenticate("jwt-auth"){
            post("logout"){
                val userid = call.getUserIdClaim() ?: run {
                    call.respond(
                        HttpStatusCode.Conflict, ErrorResponse(
                            errorCode = ErrorCode.SERVER_ERROR
                        )
                    )
                    return@post
                }

                try{
                    tokenDataSource.deleteToken(userid)
                }catch(_: Exception){
                    call.respond(
                        HttpStatusCode.Conflict, ErrorResponse(
                            errorCode = ErrorCode.SERVER_ERROR
                        )
                    )
                    return@post
                }
            }
        }
    }
}