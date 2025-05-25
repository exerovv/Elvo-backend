package com.example.database.user

import org.jetbrains.exposed.sql.Table

object UserTable: Table("users") {
    val userId = integer("userId").autoIncrement()
    val username = varchar("username", 30)
    val password= varchar("password", 100)

    override val primaryKey: PrimaryKey = PrimaryKey(userId)
}