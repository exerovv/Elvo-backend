package com.example.utils

object RecipientValidator {
    fun validatePhone(phone: String?): Boolean {
        return !(phone.isNullOrBlank()) && """^(\+7|8)[\s\-]?\(?\d{3}\)?[\s\-]?\d{3}[\s\-]?\d{2}[\s\-]?\d{2}$""".toRegex()
            .matches(phone)
    }

    fun validateFullName(name: String?, surname: String?, patronymic: String?): Boolean {
        val fullName = "${name ?: ""} ${surname ?: ""} ${patronymic ?: ""}".trim()
        return fullName.isNotBlank() && """^(?=.{1,40}$)[а-яёА-ЯЁ]+(?:[-' ][а-яёА-ЯЁ]+)*$""".toRegex()
            .matches(fullName)
    }

    fun validateAddress(city: String?, street: String?, house: Int?, flat: Int?, floor: Int?): Boolean {
        return !(city.isNullOrBlank() || street.isNullOrBlank() || house == null || flat == null || floor == null)
    }

    fun nameChanged(newName: String?, newSurname: String?, newPatronymic: String?) = newName != null || newSurname != null || newPatronymic != null

    fun addressChanged(newCity: String?, newStreet: String?, newHouse: Int?, newBuilding: String?, newFlat: Int?, newFloor: Int?) =
        newCity != null || newStreet != null || newHouse != null || newBuilding != null || newFloor != null || newFlat != null

    fun validateAllFields(
        name: String?,
        surname: String?,
        patronymic: String?,
        phone: String?,
        city: String?,
        street: String?,
        house: Int?,
        building: String?,
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
                building == null &&
                flat == null &&
                floor == null)
    }
}