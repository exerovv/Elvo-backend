package com.example.database.routing

import com.example.database.ordering.OrderingDataSource
import com.example.database.popularitems.PopularItemsDataSource
import com.example.database.recipient.RecipientDataSource
import com.example.database.address.AddressDataSource
import com.example.database.faq.FAQDataSource
import io.ktor.server.application.Application
import io.ktor.server.auth.*
import io.ktor.server.routing.routing

fun Application.dbRouting(
    orderingDataSource: OrderingDataSource,
    popularItemsDataSource: PopularItemsDataSource,
    recipientDataSource: RecipientDataSource,
    addressDataSource: AddressDataSource,
    faqDataSource: FAQDataSource
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