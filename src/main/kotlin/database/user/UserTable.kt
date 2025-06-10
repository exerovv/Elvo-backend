package com.example.database.user

import org.jetbrains.exposed.dao.id.IntIdTable

object UserTable: IntIdTable("users") {
    val username = varchar("username", 30).uniqueIndex()
    val password = varchar("password", 100)
    val avatar_url = varchar("avatar_url", 50)
}