package com.example.database.routing

import com.example.core.ErrorResponse
import com.example.database.popularitems.PopularItemsDataSource
import com.example.utils.ErrorCode
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.netty.handler.codec.http2.Http2Exception

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
        } catch (_: Http2Exception) {
            call.respond(
                HttpStatusCode.Conflict,
                ErrorResponse(ErrorCode.SERVER_ERROR)
            )
        }
    }
}