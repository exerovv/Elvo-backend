package com.example.database.ordering.table


import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object OrderStatusesTable : IntIdTable("orders_statuses") {
    val orderId = integer("order_id").references(OrderingTable.id)
    val statusId = integer("status_id").references(StatusesTable.id)
    val createdAt = timestamp("created_at")
}
