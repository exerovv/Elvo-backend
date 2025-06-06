package com.example.database.recipient

import com.example.database.address.AddressTable
import com.example.database.user.UserTable
import org.jetbrains.exposed.dao.id.IntIdTable

object RecipientTable : IntIdTable("recipients") {

    val userId = integer("user_id").references(UserTable.id)

    val name = varchar("name", 50)
    val surname = varchar("surname", 50)
    val patronymic = varchar("patronymic", 50).nullable()
    val addressId = integer("address_id").references(AddressTable.id)
    val phone = varchar("phone", 20)
}