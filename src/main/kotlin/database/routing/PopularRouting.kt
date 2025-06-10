package com.example.database.routing

import com.example.core.ErrorResponse
import com.example.database.popularitems.PopularItemsDataSource
import com.example.utils.ErrorCode
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.popularRouting(
    popularItemsDataSource: PopularItemsDataSource
){
    get("popular") {
        try {
            val result = popularItemsDataSource.getPopularItems()
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