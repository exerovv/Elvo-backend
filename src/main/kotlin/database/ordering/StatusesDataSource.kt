package com.example.database.ordering

interface StatusesDataSource {
    suspend fun getStatusById(statusId: Int): StatusDTO?
}