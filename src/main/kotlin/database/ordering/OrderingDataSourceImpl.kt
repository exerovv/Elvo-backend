package com.example.database.ordering

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction


class OrderingDataSourceImpl : OrderingDataSource {

    override suspend fun getAllOrdersForUser(userId: Int): List<OrderingShortDTO> = newSuspendedTransaction {
        OrderingTable
            .select(OrderingTable.userId eq userId)
            .map {
                OrderingShortDTO(
                    orderingId = it[OrderingTable.id].value,
                    status = it[OrderingTable.status]
                )
            }
    }

    override suspend fun getOrderById(orderingId: Int): OrderingDTO? = newSuspendedTransaction {
        OrderingTable
            .select(OrderingTable.id eq orderingId)
            .map {
                OrderingDTO(
                    orderingId = it[OrderingTable.id].value,
                    userId = it[OrderingTable.userId],
                    status = it[OrderingTable.status],
                    formationDate = it[OrderingTable.formationDate],
                    weight = it[OrderingTable.weight],
                    deliveryPrice = it[OrderingTable.deliveryPrice],
                    totalPrice = it[OrderingTable.totalPrice]
                )
            }
            .firstOrNull()
    }

    override suspend fun insertOrder(ordering: OrderingDTO): Int? {
        return try {
            newSuspendedTransaction {
                OrderingTable.insertAndGetId {
                    it[userId] = ordering.userId
                    it[status] = ordering.status
                    it[formationDate] = ordering.formationDate
                    it[weight] = ordering.weight
                    it[deliveryPrice] = ordering.deliveryPrice
                    it[totalPrice] = ordering.totalPrice
                }.value
            }
        } catch (_: Exception) {
            null
        }
    }

    override suspend fun updateOrder(ordering: OrderingDTO): Boolean = newSuspendedTransaction {
        OrderingTable.update({ OrderingTable.id eq ordering.orderingId }) {
            it[userId] = ordering.userId
            it[status] = ordering.status
            it[formationDate] = ordering.formationDate
            it[weight] = ordering.weight
            it[deliveryPrice] = ordering.deliveryPrice
            it[totalPrice] = ordering.totalPrice
        } > 0
    }
}
