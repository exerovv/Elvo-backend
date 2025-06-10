package com.example.database.ordering


import com.example.database.recipient.RecipientTable
import com.example.database.user.UserTable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object OrderingTable : IntIdTable("orders") {
    val userId = integer("user_id").references(UserTable.id)
    val recipientId = integer("recipient_id").references(RecipientTable.id)
    val createdAt = datetime("created_at")
    val weight = decimal("weight", precision = 10, scale = 2)
    val totalPrice = decimal("total_price", precision = 12, scale = 2)
    val current_status_id = integer("current_status_id").references(UserTable.id)
    val globalStatus = varchar("global_status", 32)
}
