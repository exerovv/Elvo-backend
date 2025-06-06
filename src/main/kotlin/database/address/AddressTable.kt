package com.example.database.address


import org.jetbrains.exposed.dao.id.IntIdTable

object AddressTable : IntIdTable("address") {
    val city = varchar("city", 20)
    val street = varchar("street", 20)
    val house = integer("house")
    val building = varchar("building", 5).nullable()
    val flat = integer("flat")
    val floor = integer("floor")
}
