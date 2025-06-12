package com.example.database.faq

import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction


class FaqDataSourceImpl : FaqDataSource {
    override suspend fun getFAQ(): List<FaqResponse> = newSuspendedTransaction {
        FaqTable
            .selectAll()
            .map {
                FaqResponse(
                    question = it[FaqTable.question],
                    answer = it[FaqTable.answer]
                )
            }
    }

}
