package com.example.database.recipient


import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class RecipientDataSourceImpl : RecipientDataSource {

    override suspend fun getAllRecipientsForUser(userId: Int): List<RecipientShortDTO> = newSuspendedTransaction {
        RecipientTable
            .select (RecipientTable.userId eq userId)
            .map {
                RecipientShortDTO(
                    recipientId = it[RecipientTable.recipientId],
                    name = it[RecipientTable.name]
                )
            }
    }

    override suspend fun getRecipientById(userId: Int, recipientId: Int): RecipientDTO? = newSuspendedTransaction {
        RecipientTable
            .select((RecipientTable.userId eq userId) and (RecipientTable.recipientId eq recipientId))
            .map {
                RecipientDTO(
                    recipientId = it[RecipientTable.recipientId],
                    userId = it[RecipientTable.userId],
                    name = it[RecipientTable.name],
                    address = it[RecipientTable.address],
                    phone = it[RecipientTable.phone]
                )
            }
            .firstOrNull()
    }

    override suspend fun insertRecipient(recipient: RecipientDTO): Int? {
        return try {
            newSuspendedTransaction {
                RecipientTable.insert {
                    it[userId] = recipient.userId
                    it[name] = recipient.name
                    it[address] = recipient.address
                    it[phone] = recipient.phone
                }[RecipientTable.recipientId]
            }
        } catch (_: Exception) {
            null
        }
    }


    override suspend fun updateRecipient(recipient: RecipientDTO): Boolean {
        return try {
            newSuspendedTransaction {
                RecipientTable.update({
                    (RecipientTable.userId eq recipient.userId) and (RecipientTable.recipientId eq recipient.recipientId)
                }) {
                    it[name] = recipient.name
                    it[address] = recipient.address
                    it[phone] = recipient.phone
                } > 0
            }
        } catch (_: Exception) {
            false
        }
    }

    override suspend fun deleteRecipient(userId: Int, recipientId: Int): Boolean {
        return try {
            newSuspendedTransaction {
                RecipientTable.deleteWhere {
                    (RecipientTable.userId eq userId) and (RecipientTable.recipientId eq recipientId)
                } > 0
            }
        } catch (_: Exception) {
            false
        }
    }
}
