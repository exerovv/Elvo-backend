package com.example.utils

import com.example.core.ErrorResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond

object RecipientValidator {
    fun validatePhone(phone: String): Boolean {
        return phone.isNotBlank() && """^(\+7|8)[\s\-]?\(?\d{3}\)?[\s\-]?\d{3}[\s\-]?\d{2}[\s\-]?\d{2}$""".toRegex()
            .matches(phone)
    }

    fun validateFullName(name: String, surname: String, patronymic: String?): Boolean {
        val fullName = "$name $surname ${patronymic ?: ""}".trim()

        return fullName.isNotBlank() && """^(?=.{1,40}$)[а-яёА-ЯЁ]+(?:[-' ][а-яёА-ЯЁ]+)*$""".toRegex()
            .matches(fullName)
    }

    fun validateAddress(city: String, street: String, house: Int, flat: Int, floor: Int) =
        city.isNotBlank() && street.isNotBlank() && house.toString().isNotBlank() && flat.toString()
            .isNotBlank() && floor.toString().isNotBlank()

    fun validateNameFields(name: String): Boolean{
        val nameRegex = Regex("^[A-ZА-Я][a-zа-я]+(-[A-ZА-Я][a-zа-я]+)?$")
        return nameRegex.matches(name)
    }

    fun <T> validateAddressFields(address: T): Boolean{
        return when(address){
            is Int -> address.toString().isNotBlank()
            is String -> address.isNotBlank()
            else -> false
        }
    }

    fun validateAllFields(
        name: String?,
        surname: String?,
        patronymic: String?,
        phone: String?,
        city: String?,
        street: String?,
        house: Int?,
        flat: Int?,
        floor: Int?
    ): Boolean {
        return !(name == null &&
                surname == null &&
                patronymic == null &&
                phone == null &&
                city == null &&
                street == null &&
                house == null &&
                flat == null &&
                floor == null)
    }

    suspend fun <T> ApplicationCall.validateField(
        value: T?,
        validation: (T) -> Boolean,
        errorCode: ErrorCode
    ) {
        value?.let {
            require(validation(it)) {
                respond(
                    HttpStatusCode.Conflict, ErrorResponse(
                        errorCode = errorCode
                    )
                )
            }
        }
    }
}