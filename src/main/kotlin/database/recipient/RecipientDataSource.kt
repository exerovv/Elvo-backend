package com.example.database.recipient

interface RecipientDataSource {
    suspend fun getAllRecipientsForUser(userId: Int): List<RecipientShortDTO>
    suspend fun getRecipientById(userId: Int, recipientId: Int): RecipientDTO?
    suspend fun insertRecipient(userId: Int, recipient: RecipientDTO): Int?
    suspend fun updateRecipient(userId: Int, recipientId: Int, recipient: RecipientDTO): Boolean
    suspend fun deleteRecipient(userId: Int, recipientId: Int): Boolean
    suspend fun checkRecipient(userId: Int, phone: String): Boolean
}
