package com.example.database.routing

import com.example.core.ErrorResponse
import com.example.database.faq.FaqDataSource
import com.example.utils.ErrorCode
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.faqRouting(
    faqDataSource: FaqDataSource
){
    get("faq") {
        try {
            val result = faqDataSource.getFAQ()
            call.respond(
                HttpStatusCode.OK,
                result
            )
        } catch (_: Exception) {
            call.respond(
                HttpStatusCode.Conflict,
                ErrorResponse(ErrorCode.SERVER_ERROR)
            )
        }
    }
}