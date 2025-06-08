package com.example.database.address


import org.jetbrains.exposed.dao.id.IntIdTable

object AddressTable : IntIdTable("address") {
    val city = varchar("city", 30)
    val street = varchar("street", 50)
    val house = integer("house")
    val building = varchar("building", 25).nullable()
    val flat = integer("flat")
    val floor = integer("floor")
}
