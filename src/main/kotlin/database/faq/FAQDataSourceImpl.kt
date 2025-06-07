package com.example.database.faq

import com.example.database.popularitems.PopularItemDTO
import com.example.database.popularitems.PopularItemsDataSource
import com.example.database.popularitems.PopularItemsTable
import org.jetbrains.exposed.sql.Random
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction


class FAQDataSourceImpl : FAQDataSource {
    override suspend fun getFAQ(): List<FAQItemDTO> = newSuspendedTransaction {
        FAQTable
            .selectAll()
            .map {
                FAQItemDTO(
                    question = it[FAQTable.question],
                    answer = it[FAQTable.answer]
                )
            }
    }

}
