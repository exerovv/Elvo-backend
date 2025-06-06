package com.example.database.address

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class AddressDataSourceImpl : AddressDataSource {

    override suspend fun insertAddress(address: AddressDTO): Int? = newSuspendedTransaction {
        try{
            AddressTable.insertAndGetId {
                it[city] = address.city
                it[street] = address.street
                it[house] = address.house
                it[building] = address.building
                it[flat] = address.flat
                it[floor] = address.floor
            }.value
        }catch(_: Exception){
            null
        }

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

    override suspend fun updateAddress(address: AddressDTO): Boolean {
//        return try {
//            newSuspendedTransaction {
//                AddressTable.update({ AddressTable.id eq address.id }) {
//                    it[city] = address.city
//                    it[street] = address.street
//                    it[house] = address.house
//                    it[building] = address.building
//                    it[flat] = address.flat
//                    it[floor] = address.floor
//                } > 0
//            }
//        } catch (_: Exception) {
//            false
//        }
        return false
    }

}
