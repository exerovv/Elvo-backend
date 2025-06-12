package com.example.database.popularitems

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction


class PopularItemsDataSourceImpl : PopularItemsDataSource {
    override suspend fun getPopularItems(): List<PopularItemResponse> = newSuspendedTransaction {
        PopularItemsTable
            .selectAll()
            .orderBy(Random())
            .limit(10)
            .map {
                PopularItemResponse(
                    popularItemId = it[PopularItemsTable.id].value,
                    title = it[PopularItemsTable.title],
                    url = it[PopularItemsTable.url]
                )
            }
    }

}
