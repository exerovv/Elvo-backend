package com.example.database.ordering


import com.example.database.user.UserTable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object OrderingTable : IntIdTable("ordering") {
    val userId = integer("user_id").references(UserTable.id)
    val status = varchar("status", 20)
    val formationDate = datetime("formation_date")
    val weight = decimal("weight", precision = 10, scale = 2)
    val deliveryPrice = decimal("delivery_price", precision = 10, scale = 2)
    val totalPrice = decimal("total_price", precision = 10, scale = 2)
}
