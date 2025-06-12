package com.example.utils

object OrderValidator {
    fun validateAllFields(
        recipientId: Int?,
        orderName: String?,
        trackNumber: String?,
        ruDescription: String?,
        chDescription: String?,
        itemPrice: Double?
    ): Boolean{
        return !(recipientId == null || orderName.isNullOrBlank() || trackNumber.isNullOrBlank() || ruDescription.isNullOrBlank() || chDescription.isNullOrBlank() || itemPrice == null)
    }

    fun validateLink(
        link: String?
    ): Boolean{
        val regex = """^https://dw4\.co/t/A/[a-zA-Z0-9]{9,}$""".toRegex()
        return !(link.isNullOrBlank()) && regex.matches(link)
    }

    fun validateWeightAndPrice(weight: Double?, price: Double?): Boolean{
        return !(weight == null || price == null)
    }

    fun validateUpdateStatus(id: Int): Boolean{
        return id <= 7
    }
}