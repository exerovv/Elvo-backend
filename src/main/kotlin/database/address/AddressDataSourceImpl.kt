package com.example.database.address

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class AddressDataSourceImpl : AddressDataSource {

    override suspend fun insertAddress(address: AddressDTO): Int = newSuspendedTransaction {
        AddressTable.insertAndGetId {
            it[city] = address.city
            it[street] = address.street
            it[house] = address.house
            it[building] = address.building
            it[flat] = address.flat
            it[floor] = address.floor
        }.value
    }

    override suspend fun getAddressById(addressId: Int): AddressDTO? = newSuspendedTransaction {
        AddressTable
            .selectAll()
            .where { AddressTable.id eq addressId }
            .map {
                AddressDTO(
                    city = it[AddressTable.city],
                    street = it[AddressTable.street],
                    house = it[AddressTable.house],
                    building = it[AddressTable.building],
                    flat = it[AddressTable.flat],
                    floor = it[AddressTable.floor]
                )
            }
            .singleOrNull()
    }

    override suspend fun updateAddress(addressId: Int, address: AddressDTO): Boolean =
        newSuspendedTransaction {
            AddressTable.update({ AddressTable.id eq addressId }) {
                it[city] = address.city
                it[street] = address.street
                it[house] = address.house
                it[building] = address.building
                it[flat] = address.flat
                it[floor] = address.floor
            } > 0
        }
}
