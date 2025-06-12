package com.example.database.recipient


import com.example.database.address.AddressTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class RecipientDataSourceImpl : RecipientDataSource {

    override suspend fun getAllRecipientsForUser(userId: Int): List<RecipientShortResponse> = newSuspendedTransaction {
        RecipientTable
            .selectAll()
            .where(RecipientTable.userId eq userId)
            .map {
                RecipientShortResponse(
                    recipientId = it[RecipientTable.id].value,
                    fullName = "${it[RecipientTable.name]} ${it[RecipientTable.surname]} ${it[RecipientTable.patronymic] ?: ""}".trim()
                )
            }
    }

    override suspend fun getRecipientById(userId: Int, recipientId: Int): SingleRecipientResponse? =
        newSuspendedTransaction {
            RecipientTable.join(
                otherTable = AddressTable,
                joinType = JoinType.INNER,
                additionalConstraint = { AddressTable.id eq RecipientTable.addressId }
            )
                .selectAll()
                .where((RecipientTable.userId eq userId) and (RecipientTable.id eq recipientId))
                .map {
                    val name = it[RecipientTable.name]
                    val surname = it[RecipientTable.surname]
                    val patronymic = it[RecipientTable.patronymic] ?: ""
                    val phone = it[RecipientTable.phone]
                    val city = it[AddressTable.city]
                    val street = it[AddressTable.street]
                    val house = it[AddressTable.house]
                    val building = it[AddressTable.building]
                    val flat = it[AddressTable.flat]
                    val floor = it[AddressTable.floor]
                    val fullName = "$name $surname $patronymic".trim()
                    val fullAddress = StringBuilder()
                        .append("г. $city")
                        .append(", ")
                        .append(street)
                        .append(", ")
                        .append("д. $house")
                        .append(building?.let { building -> ", ${building}, " } ?: ", ")
                        .append("кв. $flat")
                        .append(", ")
                        .append("этаж $floor")
                        .toString().trim()

                    SingleRecipientResponse(
                        name = name,
                        surname = surname,
                        patronymic = patronymic,
                        fullName = fullName,
                        phone = phone,
                        city = city,
                        street = street,
                        house = house,
                        building = building ?: "",
                        flat = flat,
                        floor = floor,
                        address = fullAddress
                    )
                }
                .firstOrNull()
        }

    override suspend fun getRecipientByIdForUpdate(
        userId: Int,
        recipientId: Int
    ): RecipientDTO? = newSuspendedTransaction {
        RecipientTable
            .selectAll()
            .where((RecipientTable.userId eq userId) and (RecipientTable.id eq recipientId))
            .map {
                RecipientDTO(
                    name = it[RecipientTable.name],
                    surname = it[RecipientTable.surname],
                    patronymic = it[RecipientTable.patronymic],
                    phone = it[RecipientTable.phone],
                    addressId = it[RecipientTable.addressId],
                )
            }
            .firstOrNull()
    }

    override suspend fun insertRecipient(userId: Int, recipient: RecipientDTO): Int = newSuspendedTransaction {
        RecipientTable.insertAndGetId {
            it[RecipientTable.userId] = userId
            it[name] = recipient.name
            it[surname] = recipient.surname
            it[patronymic] = recipient.patronymic
            it[addressId] = recipient.addressId
            it[phone] = recipient.phone
        }.value
    }

    override suspend fun updateRecipient(userId: Int, recipientId: Int, recipient: RecipientDTO): Boolean =
        newSuspendedTransaction {
            RecipientTable.update({
                (RecipientTable.userId eq userId) and (RecipientTable.id eq recipientId)
            }) {
                it[name] = recipient.name
                it[surname] = recipient.surname
                it[patronymic] = recipient.patronymic
                it[addressId] = recipient.addressId
                it[phone] = recipient.phone
            } > 0
        }

    override suspend fun deleteRecipient(userId: Int, recipientId: Int): Boolean =
        newSuspendedTransaction {
            RecipientTable.deleteWhere {
                (RecipientTable.userId eq userId) and (RecipientTable.id eq recipientId)
            } > 0
        }


    override suspend fun checkRecipient(userId: Int, phone: String): Boolean = newSuspendedTransaction {
        val result = RecipientTable
            .selectAll()
            .where((RecipientTable.userId eq userId) and (RecipientTable.phone eq phone))
            .firstOrNull()
        result == null
    }

    override suspend fun getRecipientShortById(recipientId: Int): RecipientShortResponse? = newSuspendedTransaction {
        RecipientTable
            .selectAll()
            .where { RecipientTable.id eq recipientId }
            .map {
                RecipientShortResponse(
                    recipientId = recipientId,
                    fullName = "${it[RecipientTable.name]} ${it[RecipientTable.surname]} ${it[RecipientTable.patronymic] ?: ""}".trim()
                )
            }.firstOrNull()
    }
}
