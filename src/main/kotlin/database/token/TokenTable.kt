package com.example.database.token


import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.date
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import org.jetbrains.exposed.sql.kotlin.datetime.time

object TokenTable: IntIdTable("tokens") {
    val userId = integer("user_id")
    val refreshToken = varchar("refresh_token",100).uniqueIndex()
    val issuedAt = timestamp("issued_at")
    val expiresAt = timestamp("expires_at")
    val revoked = bool("revoked")
}