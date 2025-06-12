package com.example.database.popularitems

interface PopularItemsDataSource {
    suspend fun getPopularItems(): List<PopularItemResponse>
}