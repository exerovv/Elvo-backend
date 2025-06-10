package com.example.database.ordering

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class StatusesDataSourceImpl : StatusesDataSource {
    override suspend fun getStatusById(statusId: Int): StatusDTO? = newSuspendedTransaction {
        StatusesTable
            .select(StatusesTable.id eq statusId)
            .map {
                StatusDTO(
                    code = it[StatusesTable.code],
                    name = it[StatusesTable.name],
                    globalStatus = it[StatusesTable.globalStatus],
                )
            }
            .firstOrNull()
    }
}