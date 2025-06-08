package com.example.database.routing

import com.example.database.address.AddressDataSource
import com.example.database.faq.FaqDataSource
import com.example.database.ordering.OrderingDataSource
import com.example.database.popularitems.PopularItemsDataSource
import com.example.database.recipient.RecipientDataSource
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Application.dbRouting(
    orderingDataSource: OrderingDataSource,
    popularItemsDataSource: PopularItemsDataSource,
    recipientDataSource: RecipientDataSource,
    addressDataSource: AddressDataSource,
    faqDataSource: FaqDataSource
) {
    routing {
        popularRouting(popularItemsDataSource = popularItemsDataSource)
        faqRouting(faqDataSource = faqDataSource)

        authenticate("jwt-auth") {
            recipientRouting(
                recipientDataSource = recipientDataSource,
                addressDataSource = addressDataSource
            )
        }
    }
}