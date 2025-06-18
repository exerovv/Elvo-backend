package com.example.database.ordering.datasource

import com.example.database.ordering.dto.StatusDTO

interface StatusesDataSource {
    suspend fun getStatusByCode(statusCode: String): StatusDTO?
    suspend fun getStatusById(statusId: Int): StatusDTO?
}