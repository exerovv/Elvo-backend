package com.example.database.faq

import org.jetbrains.exposed.dao.id.IntIdTable

object FaqTable : IntIdTable("faq") {
    val question = varchar("question", 75)
    val answer = varchar("answer", 250)
}
