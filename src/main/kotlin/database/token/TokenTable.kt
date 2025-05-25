package com.example.database.token


import org.jetbrains.exposed.sql.kotlin.datetime.timestamp
import org.jetbrains.exposed.sql.Table

object TokenTable: Table("tokens") {
    val id = integer("id").autoIncrement()
    val userId = integer("user_id")
    val refreshToken = varchar("refresh_token",100)
    val issuedAt = timestamp("issued_at")
    val expiresAt = timestamp("expires_at")
    val revoked = bool("revoked")

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}