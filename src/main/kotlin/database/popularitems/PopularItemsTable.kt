package com.example.database.popularitems

import org.jetbrains.exposed.dao.id.IntIdTable


object PopularItemsTable : IntIdTable("popularitems") {
    val title = varchar("title", 100)
    val url = varchar("url", 150)
}
