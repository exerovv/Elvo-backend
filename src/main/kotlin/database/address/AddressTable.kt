package com.example.database.address


import org.jetbrains.exposed.dao.id.IntIdTable

object AddressTable : IntIdTable("address") {
    val city = varchar("city", 30)
    val street = varchar("street", 30)
    val house = integer("house")
    val building = varchar("building", 15).nullable()
    val flat = integer("flat")
    val floor = integer("floor")
}
