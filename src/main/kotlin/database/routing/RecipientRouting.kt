package com.example.database.routing

import com.example.core.ErrorResponse
import com.example.database.address.AddressDTO
import com.example.database.address.AddressDataSource
import com.example.database.recipient.RecipientDTO
import com.example.database.recipient.RecipientDataSource
import com.example.database.recipient.RecipientRequest
import com.example.database.recipient.UpdateRecipientRequest
import com.example.database.token.getUserIdClaim
import com.example.utils.ErrorCode
import com.example.utils.RecipientValidator
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receiveNullable
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.lang.Exception
import kotlin.text.toInt

fun Route.recipientRouting(
    recipientDataSource: RecipientDataSource,
    addressDataSource: AddressDataSource
) {
    route("recipient") {
        post("add") {
            val userId = call.getUserIdClaim() ?: run {
                call.respond(
                    HttpStatusCode.Conflict, ErrorResponse(
                        errorCode = ErrorCode.SERVER_ERROR
                    )
                )
                return@post
            }
            val request = call.receiveNullable<RecipientRequest>()

            if (request == null) {
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
            val fullNameIsCorrect = RecipientValidator.validateFullName(name, surname, patronymic)
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

            try {
                newSuspendedTransaction {
                    val addressId = addressDataSource.insertAddress(
                        AddressDTO(
                            city = city,
                            street = street,
                            house = house,
                            building = building,
                            flat = flat,
                            floor = floor
                        )
                    )

                    recipientDataSource.insertRecipient(
                        userId = userId,
                        RecipientDTO(
                            name = name,
                            surname = surname,
                            patronymic = patronymic,
                            addressId = addressId,
                            phone = phone
                        )
                    )

                    call.respond(
                        HttpStatusCode.OK
                    )
                }
            } catch (_: Exception) {
                call.respond(
                    HttpStatusCode.Conflict, ErrorResponse(
                        errorCode = ErrorCode.SERVER_ERROR
                    )
                )
                return@post
            }
        }

        get("get") {
            val userId = call.getUserIdClaim() ?: run {
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
            val userId = call.getUserIdClaim() ?: run {
                call.respond(
                    HttpStatusCode.Conflict, ErrorResponse(
                        errorCode = ErrorCode.SERVER_ERROR
                    )
                )
                return@get
            }
            val recipientId = call.parameters["id"]?.toInt()

            if (recipientId == null) {
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
            val userid = call.getUserIdClaim() ?: run {
                call.respond(
                    HttpStatusCode.Conflict, ErrorResponse(
                        errorCode = ErrorCode.SERVER_ERROR
                    )
                )
                return@put
            }

            val recipientId = call.parameters["id"]?.toInt()

            if (recipientId == null) {
                println(1)
                call.respond(
                    HttpStatusCode.BadRequest, ErrorResponse(
                        errorCode = ErrorCode.INCORRECT_CREDENTIALS
                    )
                )
                return@put
            }

            val updateRequest = call.receiveNullable<UpdateRecipientRequest>()

            if (updateRequest == null) {
                println(2)
                call.respond(
                    HttpStatusCode.BadRequest, ErrorResponse(
                        errorCode = ErrorCode.INCORRECT_CREDENTIALS
                    )
                )
                return@put
            }

            updateRequest.run {
                if (!RecipientValidator.validateAllFields(
                        name,
                        surname,
                        patronymic,
                        phone,
                        city,
                        street,
                        house,
                        flat,
                        floor
                    )
                ) {
                    call.respond(
                        HttpStatusCode.BadRequest, ErrorResponse(
                            errorCode = ErrorCode.INCORRECT_CREDENTIALS
                        )
                    )
                    return@put
                }
            }


            val foundRecipient = recipientDataSource.getRecipientByIdForUpdate(userid, recipientId)

            if (foundRecipient == null) {
                call.respond(
                    HttpStatusCode.Conflict, ErrorResponse(
                        errorCode = ErrorCode.RECIPIENT_NOT_FOUND
                    )
                )
                return@put
            }

            val foundAddress = addressDataSource.getAddressById(foundRecipient.addressId)

            if (foundAddress == null) {
                call.respond(
                    HttpStatusCode.Conflict, ErrorResponse(
                        errorCode = ErrorCode.ADDRESS_NOT_FOUND
                    )
                )
                return@put
            }

            updateRequest.name?.let {
                require(RecipientValidator.validateNameFields(it)) {
                    call.respond(
                        HttpStatusCode.Conflict, ErrorResponse(
                            errorCode = ErrorCode.INCORRECT_NAME
                        )
                    )
                    return@put
                }
            }

            updateRequest.surname?.let {
                require(RecipientValidator.validateNameFields(it)) {
                    call.respond(
                        HttpStatusCode.Conflict, ErrorResponse(
                            errorCode = ErrorCode.INCORRECT_NAME
                        )
                    )
                    return@put
                }
            }

            updateRequest.patronymic?.let {
                require(RecipientValidator.validateNameFields(it)) {
                    call.respond(
                        HttpStatusCode.Conflict, ErrorResponse(
                            errorCode = ErrorCode.INCORRECT_NAME
                        )
                    )
                    return@put
                }
            }

            updateRequest.phone?.let {
                require(RecipientValidator.validatePhone(it)) {
                    call.respond(
                        HttpStatusCode.Conflict, ErrorResponse(
                            errorCode = ErrorCode.INCORRECT_PHONE
                        )
                    )
                    return@put
                }
            }

            updateRequest.city?.let {
                require(RecipientValidator.validateAddressFields(it)) {
                    call.respond(
                        HttpStatusCode.Conflict, ErrorResponse(
                            errorCode = ErrorCode.INCORRECT_ADDRESS
                        )
                    )
                    return@put
                }
            }

            updateRequest.street?.let {
                require(RecipientValidator.validateAddressFields(it)) {
                    call.respond(
                        HttpStatusCode.Conflict, ErrorResponse(
                            errorCode = ErrorCode.INCORRECT_ADDRESS
                        )
                    )
                    return@put
                }
            }

            updateRequest.house?.let {
                require(RecipientValidator.validateAddressFields(it)) {
                    call.respond(
                        HttpStatusCode.Conflict, ErrorResponse(
                            errorCode = ErrorCode.INCORRECT_ADDRESS
                        )
                    )
                    return@put
                }
            }

            updateRequest.building?.let {
                require(RecipientValidator.validateAddressFields(it)) {
                    call.respond(
                        HttpStatusCode.Conflict, ErrorResponse(
                            errorCode = ErrorCode.INCORRECT_ADDRESS
                        )
                    )
                    return@put
                }
            }

            updateRequest.flat?.let {
                require(RecipientValidator.validateAddressFields(it)) {
                    call.respond(
                        HttpStatusCode.Conflict, ErrorResponse(
                            errorCode = ErrorCode.INCORRECT_ADDRESS
                        )
                    )
                    return@put
                }
            }

            updateRequest.floor?.let {
                require(RecipientValidator.validateAddressFields(it)) {
                    call.respond(
                        HttpStatusCode.Conflict, ErrorResponse(
                            errorCode = ErrorCode.INCORRECT_ADDRESS
                        )
                    )
                    return@put
                }
            }

            val updatedRecipient = foundRecipient.copy(
                name = updateRequest.name ?: foundRecipient.name,
                surname = updateRequest.surname ?: foundRecipient.surname,
                patronymic = updateRequest.patronymic ?: foundRecipient.patronymic,
                phone = updateRequest.phone ?: foundRecipient.phone,
            )

            val updatedAddress = foundAddress.copy(
                city = updateRequest.city ?: foundAddress.city,
                street = updateRequest.street ?: foundAddress.street,
                house = updateRequest.house ?: foundAddress.house,
                building = updateRequest.building ?: foundAddress.building,
                flat = updateRequest.flat ?: foundAddress.flat,
                floor = updateRequest.floor ?: foundAddress.floor,
            )

            var isSuccess = true
            if (updatedRecipient != foundRecipient) {
                isSuccess = recipientDataSource.updateRecipient(userid, recipientId, updatedRecipient)
            }

            if (updatedAddress != foundAddress) {
                isSuccess = addressDataSource.updateAddress(foundRecipient.addressId, updatedAddress)
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