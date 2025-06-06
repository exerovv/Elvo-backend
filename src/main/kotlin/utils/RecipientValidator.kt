package com.example.utils

object RecipientValidator {
    fun validatePhone(phone: String): Boolean {
        return phone.isNotBlank() && """^(\+7|8)[\s\-]?\(?\d{3}\)?[\s\-]?\d{3}[\s\-]?\d{2}[\s\-]?\d{2}$""".toRegex()
            .matches(phone)
    }

    fun validateName(name: String, surname: String, patronymic: String?): Boolean {

        val fullName = "$name $surname ${patronymic ?: ""}".trim()

        return fullName.isNotBlank() && """^(?=.{1,40}$)[а-яёА-ЯЁ]+(?:[-' ][а-яёА-ЯЁ]+)*$""".toRegex()
            .matches(fullName)
    }

    fun validateAddress(city: String, street: String, house: Int, flat: Int, floor: Int) =
        city.isNotBlank() && street.isNotBlank() && house.toString().isNotBlank() && flat.toString()
            .isNotBlank() && floor.toString().isNotBlank()
}