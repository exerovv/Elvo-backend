package com.example.database.ordering.table

import org.jetbrains.exposed.dao.id.IntIdTable

object StatusesTable : IntIdTable("statuses") {
    val code = varchar("code", 50)
    val name = varchar("name", 50)
    val globalStatus = varchar("global_status", 50)
    val icon = varchar("icon", 150)
}
