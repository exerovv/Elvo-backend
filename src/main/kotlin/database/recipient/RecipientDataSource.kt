package com.example.database.recipient

interface RecipientDataSource {
    suspend fun getAllRecipientsForUser(userId: Int): List<RecipientShortDTO>
    suspend fun getRecipientById(userId: Int, recipientId: Int): RecipientDTO?
    suspend fun insertRecipient(recipient: RecipientDTO): Int?
    suspend fun updateRecipient(recipient: RecipientDTO): Boolean
    suspend fun deleteRecipient(userId: Int, recipientId: Int): Boolean
}
