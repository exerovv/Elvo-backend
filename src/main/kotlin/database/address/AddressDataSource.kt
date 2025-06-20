package com.example.database.address


interface AddressDataSource {
    suspend fun insertAddress(address: AddressDTO): Int
    suspend fun getAddressById(addressId: Int): AddressDTO?
    suspend fun updateAddress(addressId: Int, address: AddressDTO): Boolean

}
