package com.example.database.faq

import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction


class FaqDataSourceImpl : FaqDataSource {
    override suspend fun getFAQ(): List<FaqDTO> = newSuspendedTransaction {
        FaqTable
            .selectAll()
            .map {
                FaqDTO(
                    question = it[FaqTable.question],
                    answer = it[FaqTable.answer]
                )
            }
    }

}
