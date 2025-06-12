package com.example.database.faq


interface FaqDataSource {
    suspend fun getFAQ(): List<FaqResponse>
}