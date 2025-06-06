package com.example.database.routing

import com.example.database.ordering.OrderingDataSource
import com.example.database.popularitems.PopularItemsDataSource
import com.example.database.recipient.RecipientDataSource
import com.example.core.ErrorResponse
import com.example.database.recipient.RecipientDTO
import com.example.database.recipient.RecipientRequest
import com.example.utils.ErrorCode
import com.example.utils.RecipientValidator
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receiveNullable
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.netty.handler.codec.http2.Http2Exception
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

fun Application.dbRouting(
    orderingDataSource: OrderingDataSource,
    popularItemsDataSource: PopularItemsDataSource,
    recipientDataSource: RecipientDataSource
) {
    routing {
        get("popular") {
            try {
                val result = popularItemsDataSource.getPopularItems()
                call.respond(
                    HttpStatusCode.OK,
                    result
                )
            } catch (_: Http2Exception) {
                call.respond(
                    HttpStatusCode.Conflict,
                    ErrorResponse(ErrorCode.SERVER_ERROR)
                )
            }
        }
    }

    authenticate("jwt-auth") {
        route("recipient"){
            post("add") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.getClaim("userId", Int::class) ?: run {
                    call.respond(
                        HttpStatusCode.Conflict, ErrorResponse(
                            errorCode = ErrorCode.SERVER_ERROR
                        )
                    )
                    return@post
                }
                val request =
                    runCatching<RecipientRequest?> { call.receiveNullable<RecipientRequest>() }.getOrNull() ?: run {
                        call.respond(
                            HttpStatusCode.BadRequest, ErrorResponse(
                                errorCode = ErrorCode.INCORRECT_CREDENTIALS
                            )
                        )
                        return@post
                    }

                val name = request.name
                val surname = request.surname
                val patronymic = request.patronymic
                val phone = request.phone
                val city: String = request.city
                val street: String = request.street
                val house: Int = request.house
                val building: String? = request.building
                val flat: Int = request.flat
                val floor: Int = request.floor

                val phoneIsCorrect = RecipientValidator.validatePhone(phone)
                val fullNameIsCorrect = RecipientValidator.validateName(name, surname, patronymic)
                val addressIsCorrect = RecipientValidator.validateAddress(city, street, house, flat, floor)

                if (!phoneIsCorrect) {
                    call.respond(
                        HttpStatusCode.BadRequest, ErrorResponse(
                            errorCode = ErrorCode.INCORRECT_PHONE
                        )
                    )
                    return@post
                }
                if (!fullNameIsCorrect) {
                    call.respond(
                        HttpStatusCode.BadRequest, ErrorResponse(
                            errorCode = ErrorCode.INCORRECT_NAME
                        )
                    )
                    return@post
                }
                if (!addressIsCorrect) {
                    call.respond(
                        HttpStatusCode.BadRequest, ErrorResponse(
                            errorCode = ErrorCode.INCORRECT_ADDRESS
                        )
                    )
                    return@post
                }

                val checkRecipient = recipientDataSource.checkRecipient(userId, phone)

                if (!checkRecipient) {
                    call.respond(
                        HttpStatusCode.BadRequest, ErrorResponse(
                            errorCode = ErrorCode.RECIPIENT_ALREADY_EXISTS
                        )
                    )
                    return@post
                }

                val recipientId = newSuspendedTransaction {
                    val recipientId = recipientDataSource.insertRecipient(
                        RecipientDTO(
                            name = name,
                            surname = surname,
                            patronymic = patronymic,
                            city = city,
                            street = street,
                            house = house,
                            building = building,
                            flat = flat,
                            floor = floor,
                            phone = phone
                        )
                    )
                    recipientId?.let {
//                    tokenDataSource.insertToken(
//                        Token(
//                            userId,
//                            refreshToken,
//                            Clock.System.now(),
//                            Clock.System.now().plus(refreshTokenConfig.expiresIn.milliseconds),
//                            false
//                        )
//                    )
                    }
                    recipientId
                }

                if (recipientId == null) {
                    call.respond(
                        HttpStatusCode.Conflict, ErrorResponse(
                            errorCode = ErrorCode.SERVER_ERROR
                        )
                    )
                    return@post
                }

                call.respond(
                    HttpStatusCode.OK
                )
            }
            get("get") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.getClaim("userId", Int::class) ?: run {
                    call.respond(
                        HttpStatusCode.Conflict, ErrorResponse(
                            errorCode = ErrorCode.SERVER_ERROR
                        )
                    )
                    return@get
                }
                val result = recipientDataSource.getAllRecipientsForUser(userId)
                call.respond(
                    HttpStatusCode.OK,
                    message = result
                )
            }
            get("{id}/get") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.getClaim("userId", Int::class) ?: run {
                    call.respond(
                        HttpStatusCode.Conflict, ErrorResponse(
                            errorCode = ErrorCode.SERVER_ERROR
                        )
                    )
                    return@get
                }
                val recipientId = call.parameters["id"]?.toInt() ?: run {
                    call.respond(
                        HttpStatusCode.BadRequest, ErrorResponse(
                            errorCode = ErrorCode.INCORRECT_CREDENTIALS
                        )
                    )
                    return@get
                }

                val result = recipientDataSource.getRecipientById(userId, recipientId)

                if (result == null) {
                    call.respond(
                        HttpStatusCode.Conflict, ErrorResponse(
                            errorCode = ErrorCode.RECIPIENT_NOT_FOUND
                        )
                    )
                    return@get
                }
                call.respond(
                    HttpStatusCode.OK,
                    message = result
                )
            }
            put("{id}/update") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.getClaim("userId", Int::class) ?: run {
                    call.respond(
                        HttpStatusCode.Conflict, ErrorResponse(
                            errorCode = ErrorCode.SERVER_ERROR
                        )
                    )
                    return@put
                }
                val recipientId = call.parameters["id"]?.toInt() ?: run {
                    call.respond(
                        HttpStatusCode.BadRequest, ErrorResponse(
                            errorCode = ErrorCode.INCORRECT_CREDENTIALS
                        )
                    )
                    return@put
                }
                val request =
                    runCatching<RecipientRequest?> { call.receiveNullable<RecipientRequest>() }.getOrNull() ?: run {
                        call.respond(
                            HttpStatusCode.BadRequest, ErrorResponse(
                                errorCode = ErrorCode.INCORRECT_CREDENTIALS
                            )
                        )
                        return@put
                    }

                val name = request.name
                val surname = request.surname
                val patronymic = request.patronymic
                val phone = request.phone
                val city: String = request.city
                val street: String = request.street
                val house: Int = request.house
                val building: String? = request.building
                val flat: Int = request.flat
                val floor: Int = request.floor

                val phoneIsCorrect = RecipientValidator.validatePhone(phone)
                val fullNameIsCorrect = RecipientValidator.validateName(name, surname, patronymic)
                val addressIsCorrect = RecipientValidator.validateAddress(city, street, house, flat, floor)

                if (!phoneIsCorrect) {
                    call.respond(
                        HttpStatusCode.BadRequest, ErrorResponse(
                            errorCode = ErrorCode.INCORRECT_PHONE
                        )
                    )
                    return@put
                }
                if (!fullNameIsCorrect) {
                    call.respond(
                        HttpStatusCode.BadRequest, ErrorResponse(
                            errorCode = ErrorCode.INCORRECT_NAME
                        )
                    )
                    return@put
                }
                if (!addressIsCorrect) {
                    call.respond(
                        HttpStatusCode.BadRequest, ErrorResponse(
                            errorCode = ErrorCode.INCORRECT_ADDRESS
                        )
                    )
                    return@put
                }

                val isSuccess = newSuspendedTransaction {
                    val isSuccess = recipientDataSource.updateRecipient(
                        userId,
                        recipientId,
                        RecipientDTO(
                            name = name,
                            surname = surname,
                            patronymic = patronymic,
                            city = city,
                            street = street,
                            house = house,
                            building = building,
                            flat = flat,
                            floor = floor,
                            phone = phone
                        )
                    )
                    if (isSuccess) {
                        TODO()
                    }
                    isSuccess
                }

                if (!isSuccess) {
                    call.respond(
                        HttpStatusCode.Conflict, ErrorResponse(
                            errorCode = ErrorCode.SERVER_ERROR
                        )
                    )
                    return@put
                }

                call.respond(
                    HttpStatusCode.OK
                )
            }
        }
    }
}