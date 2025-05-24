package com.example.database.models

import org.jetbrains.exposed.sql.Table

object UserTable: Table("users") {
    val username = varchar("username", 30)
    val password= varchar("password", 50)
    val salt= varchar("salt", 25)

    override val primaryKey: PrimaryKey = PrimaryKey(username)
}