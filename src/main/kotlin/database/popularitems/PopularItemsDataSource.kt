package com.example.database.popularitems

interface PopularItemsDataSource {
    suspend fun getRandomPopularItems(): List<PopularItemDTO>
}