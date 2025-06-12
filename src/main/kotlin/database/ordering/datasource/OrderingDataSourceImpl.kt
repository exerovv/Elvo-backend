package com.example.database.ordering.datasource

import com.example.database.ordering.dto.OrderDTO
import com.example.database.ordering.dto.UpdateOrderDTO
import com.example.database.ordering.response.OrderFullResponse
import com.example.database.ordering.response.OrderPaymentStatusResponse
import com.example.database.ordering.response.OrderShortResponse
import com.example.database.ordering.table.OrderingTable
import com.example.database.ordering.table.StatusesTable
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update


class OrderingDataSourceImpl : OrderingDataSource {

    override suspend fun getAllOrdersForUser(userId: Int): List<OrderShortResponse> = newSuspendedTransaction {
        OrderingTable
            .join(
                StatusesTable,
                JoinType.INNER,
                onColumn = OrderingTable.current_status_id,
                otherColumn = StatusesTable.id
            )
            .selectAll()
            .where { OrderingTable.userId eq userId }
            .map {
                OrderShortResponse(
                    orderingId = it[OrderingTable.id].value,
                    orderName = it[OrderingTable.orderName],
                    status = it[StatusesTable.name],
                    globalStatus = it[OrderingTable.globalStatus],
                    isPaid = it[OrderingTable.isPaid]
                )
            }
    }


    override suspend fun getOrderFullById(orderingId: Int): OrderFullResponse? = newSuspendedTransaction {
        OrderingTable
            .join(StatusesTable, JoinType.INNER, OrderingTable.current_status_id, StatusesTable.id)
            .selectAll()
            .where(OrderingTable.id eq orderingId)
            .map {
                OrderFullResponse(
                    recipientId = it[OrderingTable.recipientId],
                    orderName = it[OrderingTable.orderName],
                    trackNumber = it[OrderingTable.trackNumber],
                    currentStatus = it[StatusesTable.name],
                    createdAt = it[OrderingTable.createdAt],
                    weight = it[OrderingTable.weight],
                    totalPrice = it[OrderingTable.totalPrice],
                    globalStatus = it[StatusesTable.globalStatus],
                    isPaid = it[OrderingTable.isPaid],
                    ruDescription = it[OrderingTable.ruDescription],
                    chDescription = it[OrderingTable.chDescription],
                    link = it[OrderingTable.link],
                    itemPrice = it[OrderingTable.itemPrice]
                )
            }
            .firstOrNull()
    }

    override suspend fun getOrderShortById(orderingId: Int): OrderShortResponse? = newSuspendedTransaction {
        OrderingTable
            .join(
                StatusesTable,
                JoinType.INNER,
                onColumn = OrderingTable.current_status_id,
                otherColumn = StatusesTable.id
            )
            .selectAll()
            .where { OrderingTable.id eq orderingId }
            .map {
                OrderShortResponse(
                    orderingId = it[OrderingTable.id].value,
                    orderName = it[OrderingTable.orderName],
                    status = it[StatusesTable.name],
                    globalStatus = it[OrderingTable.globalStatus],
                    isPaid = it[OrderingTable.isPaid]
                )
            }
            .firstOrNull()
    }


    override suspend fun insertOrder(userId: Int, order: OrderDTO): Int =
        newSuspendedTransaction {
            OrderingTable.insertAndGetId {
                it[OrderingTable.userId] = userId
                it[OrderingTable.recipientId] = order.recipientId
                it[OrderingTable.orderName] = order.orderName
                it[OrderingTable.trackNumber] = order.trackNumber
                it[OrderingTable.createdAt] = order.createdAt
                it[OrderingTable.current_status_id] = order.currentStatusId
                it[OrderingTable.globalStatus] = order.globalStatus
                it[OrderingTable.isPaid] = order.isPaid
                it[OrderingTable.ruDescription] = order.ruDescription
                it[OrderingTable.chDescription] = order.chDescription
                it[OrderingTable.link] = order.link
                it[OrderingTable.itemPrice] = order.itemPrice
            }.value
        }

    override suspend fun updateOrder(orderId: Int, updateOrderingDTO: UpdateOrderDTO): Boolean =
        newSuspendedTransaction {
            OrderingTable.update({ OrderingTable.id eq orderId }) {
                updateOrderingDTO.weight?.let { weight -> it[OrderingTable.weight] = weight }
                updateOrderingDTO.totalPrice?.let { price -> it[OrderingTable.totalPrice] = price }
                it[OrderingTable.current_status_id] = updateOrderingDTO.updateStatusId
                updateOrderingDTO.paymentStatus?.let { paymentStatus -> it[OrderingTable.isPaid] = paymentStatus }
            } > 0
        }

    override suspend fun makePayment(orderId: Int, paymentStatus: String): Boolean = newSuspendedTransaction {
        OrderingTable.update({ OrderingTable.id eq orderId }) {
            it[OrderingTable.isPaid] = paymentStatus
        } > 0
    }

    override suspend fun getPaymentStatusesForArrivedOrders(paymentStatus: String): List<OrderPaymentStatusResponse> {
        return newSuspendedTransaction {
            OrderingTable
                .selectAll()
                .where { OrderingTable.isPaid eq paymentStatus }
                .map {
                    OrderPaymentStatusResponse(
                        it[OrderingTable.id].value,
                        it[OrderingTable.isPaid]
                    )
                }
        }
    }
}
