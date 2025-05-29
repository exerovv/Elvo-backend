package com.example.database.ordering


import com.example.database.address.AddressTable
import com.example.database.user.UserTable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object OrderingTable: IntIdTable("ordering") {
    val userId = integer("user_id").references(UserTable.id)
    val status = varchar("status",20)
    val formationDate = timestamp("formation_date")
    val addressId = integer("address_id").references(AddressTable.id)
}