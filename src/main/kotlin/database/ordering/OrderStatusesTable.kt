package com.example.database.ordering


import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object OrderStatusesTable : IntIdTable("orders_statuses") {
    val orderId = integer("order_id").references(OrderingTable.id)
    val statusId = integer("status_id").references(StatusesTable.id)
    val createdAt = datetime("created_at")
}
