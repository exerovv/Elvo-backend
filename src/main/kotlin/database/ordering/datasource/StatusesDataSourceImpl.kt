package com.example.database.ordering.datasource

import com.example.database.ordering.dto.StatusDTO
import com.example.database.ordering.table.StatusesTable
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class StatusesDataSourceImpl : StatusesDataSource {
    override suspend fun getStatusByCode(statusCode: String): StatusDTO? = newSuspendedTransaction {
        StatusesTable
            .selectAll()
            .where { StatusesTable.code eq statusCode }
            .map {
                StatusDTO(
                    id = it[StatusesTable.id].value,
                    name = it[StatusesTable.name],
                    globalStatus = it[StatusesTable.globalStatus]
                )
            }
            .firstOrNull()
    }
}