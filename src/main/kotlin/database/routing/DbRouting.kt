package com.example.database.routing

import com.example.database.ordering.OrderingDataSource
import com.example.database.popularitems.PopularItemsDataSource
import com.example.database.recipient.RecipientDataSource
import com.example.database.address.AddressDataSource
import io.ktor.server.application.Application
import io.ktor.server.auth.*
import io.ktor.server.routing.routing

fun Application.dbRouting(
    orderingDataSource: OrderingDataSource,
    popularItemsDataSource: PopularItemsDataSource,
    recipientDataSource: RecipientDataSource,
    addressDataSource: AddressDataSource
) {
    routing {
        popularRouting(popularItemsDataSource = popularItemsDataSource)

        authenticate("jwt-auth") {
            recipientRouting(
                recipientDataSource = recipientDataSource,
                addressDataSource = addressDataSource
            )
        }
    }
}