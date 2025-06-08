package com.example.database.recipient


import com.example.database.address.AddressTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class RecipientDataSourceImpl : RecipientDataSource {

    override suspend fun getAllRecipientsForUser(userId: Int): List<RecipientShortDTO> = newSuspendedTransaction {
        RecipientTable
            .selectAll()
            .where(RecipientTable.userId eq userId)
            .map {
                RecipientShortDTO(
                    recipientId = it[RecipientTable.id].value,
                    name = it[RecipientTable.name]
                )
            }
    }

    override suspend fun getRecipientById(userId: Int, recipientId: Int): SingleRecipientResponse? = newSuspendedTransaction {
        RecipientTable.join(
            otherTable = AddressTable,
            joinType = JoinType.INNER,
            additionalConstraint = { AddressTable.id eq RecipientTable.addressId}
        )
            .selectAll()
            .where((RecipientTable.userId eq userId) and (RecipientTable.id eq recipientId))
            .map {
                val fullName = "${it[RecipientTable.name]} ${it[RecipientTable.surname]} ${it[RecipientTable.patronymic] ?: ""}".trim()
                val fullAddress = StringBuilder()
                    .append("г. ${it[AddressTable.city]}")
                    .append(", ")
                    .append(it[AddressTable.street])
                    .append(", ")
                    .append("д. ${it[AddressTable.house]}")
                    .append(it[AddressTable.building]?.let { building -> ", ${building}, " } ?: ", ")
                    .append("кв. ${it[AddressTable.flat]}")
                    .append(", ")
                    .append("этаж ${it[AddressTable.floor]}")
                    .toString().trim()

                SingleRecipientResponse(
                    fullName = fullName,
                    phone = it[RecipientTable.phone],
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
}
