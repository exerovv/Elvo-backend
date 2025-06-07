package com.example.database.faq


interface FAQDataSource {
    suspend fun getFAQ(): List<FAQItemDTO>
}