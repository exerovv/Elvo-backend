package com.example.database.ordering.table


import com.example.database.recipient.RecipientTable
import com.example.database.user.UserTable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object OrderingTable : IntIdTable("orders") {
    val userId = integer("user_id").references(UserTable.id)
    val recipientId = integer("recipient_id").references(RecipientTable.id)
    val orderName = varchar("order_name", 50)
    val trackNumber = varchar("track_number", 50)
    val createdAt = timestamp("created_at")
    val weight = double("weight").nullable()
    val totalPrice = double("total_price").nullable()
    val current_status_id = integer("current_status_id").references(StatusesTable.id)
    val globalStatus = varchar("global_status", 32)
    val isPaid = varchar("is_paid", 20)
    val ruDescription = varchar("ru_description", 100)
    val chDescription = varchar("ch_description", 100)
    val link = varchar("poizon_link", 50)
    val itemPrice = double("item_price")
}
