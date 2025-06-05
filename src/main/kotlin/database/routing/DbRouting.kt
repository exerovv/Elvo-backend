package com.example.database.routing

import com.example.database.ordering.OrderingDataSource
import com.example.database.popularitems.PopularItemsDataSource
import com.example.database.recipient.RecipientDataSource
import com.example.core.ErrorResponse
import com.example.utils.ErrorCode
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.netty.handler.codec.http2.Http2Exception

fun Application.dbRouting(
    orderingDataSource: OrderingDataSource,
    popularItemsDataSource: PopularItemsDataSource,
    recipientDataSource: RecipientDataSource
){
    routing{
        get("popular"){
            try{
                val result = popularItemsDataSource.getPopularItems()
                call.respond(
                    HttpStatusCode.OK,
                    result
                )
            }catch (_: Http2Exception){
                call.respond(
                    HttpStatusCode.Conflict,
                    ErrorResponse(ErrorCode.SERVER_ERROR)
                )
            }
        }
    }

//    authenticate("jwt-auth"){
//        post("")
//    }
}