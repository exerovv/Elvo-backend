package com.example.database.ordering.datasource

import com.example.database.ordering.dto.OrderStatusDTO
import com.example.database.ordering.response.OrderStatusResponse
import com.example.database.ordering.table.OrderStatusesTable
import com.example.database.ordering.table.StatusesTable
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class OrderStatusesDataSourceImpl: OrderStatusesDataSource {
    override suspend fun insertNewStatus(newRecord: OrderStatusDTO) {
        return newSuspendedTransaction {
            OrderStatusesTable.insert {
                it[OrderStatusesTable.orderId] = orderId
                it[OrderStatusesTable.statusId] = newRecord.statusId
                it[OrderStatusesTable.createdAt] = newRecord.createdAt
            }
        }
    }

    override suspend fun getStatusHistory(orderId: Int): List<OrderStatusResponse> {
        return newSuspendedTransaction {
            OrderStatusesTable
                .join(
                    StatusesTable,
                    joinType = JoinType.INNER,
                    OrderStatusesTable.statusId eq StatusesTable.id
                )
                .selectAll()
                .where{ OrderStatusesTable.orderId eq orderId }
                .map {
                    OrderStatusResponse(
                        it[StatusesTable.name],
                        it[StatusesTable.name],
                        it[OrderStatusesTable.createdAt]
                    )
                }
        }
    }
}