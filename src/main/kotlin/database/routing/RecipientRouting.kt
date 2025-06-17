package com.example.database.routing

import com.example.core.ErrorResponse
import com.example.database.address.AddressDTO
import com.example.database.address.AddressDataSource
import com.example.database.recipient.*
import com.example.database.token.getUserIdClaim
import com.example.utils.ErrorCode
import com.example.utils.RecipientValidator
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

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
            val city = request.city
            val street = request.street
            val house = request.house
            val building = request.building
            val flat = request.flat
            val floor = request.floor

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

            val checkRecipient = recipientDataSource.checkRecipient(userId, phone!!)

            if (!checkRecipient) {
                call.respond(
                    HttpStatusCode.BadRequest, ErrorResponse(
                        errorCode = ErrorCode.RECIPIENT_ALREADY_EXISTS
                    )
                )
                return@post
            }

            val insertedRecipient = try {
                newSuspendedTransaction {
                    val addressId = addressDataSource.insertAddress(
                        AddressDTO(
                            city = city!!,
                            street = street!!,
                            house = house!!,
                            building = building,
                            flat = flat!!,
                            floor = floor!!
                        )
                    )

                    val recipientId = recipientDataSource.insertRecipient(
                        userId = userId,
                        RecipientDTO(
                            name = name!!,
                            surname = surname!!,
                            patronymic = patronymic,
                            addressId = addressId,
                            phone = phone
                        )
                    )

                    recipientDataSource.getRecipientShortById(recipientId)
                }
            } catch (_: Exception) {
                call.respond(
                    HttpStatusCode.Conflict, ErrorResponse(
                        errorCode = ErrorCode.SERVER_ERROR
                    )
                )
                return@post
            }

            if (insertedRecipient == null) {
                call.respond(
                    HttpStatusCode.Conflict, ErrorResponse(
                        errorCode = ErrorCode.SERVER_ERROR
                    )
                )
                return@post
            }

            call.respond(
                status = HttpStatusCode.OK,
                message = insertedRecipient
            )
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

        get("get/{id}") {
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

            try{
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
            }catch(_: Exception){
                call.respond(
                    HttpStatusCode.Conflict, ErrorResponse(
                        errorCode = ErrorCode.SERVER_ERROR
                    )
                )
                return@get
            }
        }

        put("update/{id}") {
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
                call.respond(
                    HttpStatusCode.BadRequest, ErrorResponse(
                        errorCode = ErrorCode.INCORRECT_CREDENTIALS
                    )
                )
                return@put
            }

            val name = updateRequest.name
            val surname = updateRequest.surname
            val patronymic = updateRequest.patronymic
            val phone = updateRequest.phone
            val city = updateRequest.city
            val street = updateRequest.street
            val house = updateRequest.house
            val building = updateRequest.building
            val flat = updateRequest.flat
            val floor = updateRequest.floor

            if (RecipientValidator.validateAllFields(
                    name,
                    surname,
                    patronymic,
                    phone,
                    city,
                    street,
                    house,
                    building,
                    flat,
                    floor
                )
            ) {
                call.respond(
                    HttpStatusCode.BadRequest, ErrorResponse(
                        errorCode = ErrorCode.NOTHING_CHANGED
                    )
                )
                return@put
            }

            val foundRecipient = try{
                recipientDataSource.getRecipientByIdForUpdate(userid, recipientId)
            }catch (_: Exception){
                call.respond(
                    HttpStatusCode.Conflict, ErrorResponse(
                        errorCode = ErrorCode.SERVER_ERROR
                    )
                )
                return@put
            }

            if (foundRecipient == null) {
                call.respond(
                    HttpStatusCode.Conflict, ErrorResponse(
                        errorCode = ErrorCode.RECIPIENT_NOT_FOUND
                    )
                )
                return@put
            }

            val foundAddress = try{
                addressDataSource.getAddressById(foundRecipient.addressId)
            }catch (_: Exception){
                call.respond(
                    HttpStatusCode.Conflict, ErrorResponse(
                        errorCode = ErrorCode.SERVER_ERROR
                    )
                )
                return@put
            }

            if (foundAddress == null) {
                call.respond(
                    HttpStatusCode.Conflict, ErrorResponse(
                        errorCode = ErrorCode.ADDRESS_NOT_FOUND
                    )
                )
                return@put
            }

            val nameChanged = RecipientValidator.nameChanged(name, surname, patronymic)

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

            if (nameChanged) {
                if (!RecipientValidator.validateFullName(
                        name = name ?: foundRecipient.name,
                        surname = surname ?: foundRecipient.surname,
                        patronymic = patronymic ?: foundRecipient.patronymic
                    )
                ) {
                    call.respond(
                        HttpStatusCode.Conflict, ErrorResponse(
                            errorCode = ErrorCode.INCORRECT_NAME
                        )
                    )
                    return@put
                }
            }

            val addressChanged = RecipientValidator.addressChanged(city, street, house, building, flat, floor)

            if (addressChanged){
                if (!RecipientValidator.validateAddress(
                        city = city ?: foundAddress.city,
                        street = street ?: foundAddress.street,
                        house = house ?: foundAddress.house,
                        flat = flat ?: foundAddress.flat,
                        floor = floor ?: foundAddress.floor,
                    )
                ) {
                    call.respond(
                        HttpStatusCode.BadRequest, ErrorResponse(
                            errorCode = ErrorCode.INCORRECT_ADDRESS
                        )
                    )
                    return@put
                }
            }


            if (updateRequest.phone != null || nameChanged) {
                val updatedRecipient = foundRecipient.copy(
                    name = name ?: foundRecipient.name,
                    surname = surname ?: foundRecipient.surname,
                    patronymic = patronymic ?: foundRecipient.patronymic,
                    phone = phone ?: foundRecipient.phone,
                )

                if (updatedRecipient != foundRecipient) {
                    try {
                        recipientDataSource.updateRecipient(userid, recipientId, updatedRecipient)
                        if (nameChanged) {
                            call.respond(
                                HttpStatusCode.OK,
                                UpdateResponse(
                                    RecipientShortResponse(
                                        recipientId = recipientId,
                                        fullName = "${updatedRecipient.name} ${updatedRecipient.surname} ${updatedRecipient.patronymic ?: ""}".trim()
                                    )
                                )
                            )
                        } else {
                            call.respond(
                                HttpStatusCode.OK,
                                UpdateResponse()
                            )
                        }

                    } catch (_: Exception) {
                        call.respond(
                            HttpStatusCode.Conflict, ErrorResponse(
                                errorCode = ErrorCode.SERVER_ERROR
                            )
                        )
                        return@put
                    }
                }
            }

            val updatedAddress = foundAddress.copy(
                city = city ?: foundAddress.city,
                street = street ?: foundAddress.street,
                house = house ?: foundAddress.house,
                building = building ?: foundAddress.building,
                flat = flat ?: foundAddress.flat,
                floor = floor ?: foundAddress.floor,
            )


            if (updatedAddress != foundAddress) {
                try {
                    addressDataSource.updateAddress(foundRecipient.addressId, updatedAddress)
                    call.respond(
                        HttpStatusCode.OK,
                        UpdateResponse()
                    )
                } catch (_: Exception) {
                    call.respond(
                        HttpStatusCode.Conflict, ErrorResponse(
                            errorCode = ErrorCode.SERVER_ERROR
                        )
                    )
                    return@put
                }
            }
        }
    }

}