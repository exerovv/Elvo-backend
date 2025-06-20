package com.example

import com.example.database.address.AddressDataSource
import com.example.database.faq.FaqDataSource
import com.example.database.ordering.datasource.OrderStatusesDataSource
import com.example.database.ordering.datasource.OrderingDataSource
import com.example.database.ordering.datasource.StatusesDataSource
import com.example.database.popularitems.PopularItemsDataSource
import com.example.database.recipient.RecipientDataSource
import com.example.database.routing.dbRouting
import com.example.database.token.TokenDataSource
import com.example.database.user.UserDataSource
import com.example.security.hashing.HashingService
import com.example.security.routing.authRouting
import com.example.security.token.JWTTokenConfig
import com.example.security.token.JwtTokenService
import com.example.security.token.RefreshTokenConfig
import com.example.security.token.RefreshTokenService
import io.ktor.server.application.*

fun Application.configureRouting(
    userDataSource: UserDataSource,
    hashingService: HashingService,
    jwtTokenService: JwtTokenService,
    jwtTokenConfig: JWTTokenConfig,
    refreshTokenConfig: RefreshTokenConfig,
    refreshTokenService: RefreshTokenService,
    tokenDataSource: TokenDataSource,
    orderingDataSource: OrderingDataSource,
    popularItemsDataSource: PopularItemsDataSource,
    recipientDataSource: RecipientDataSource,
    addressDataSource: AddressDataSource,
    faqDataSource: FaqDataSource,
    orderStatusesDataSource: OrderStatusesDataSource,
    statusesDataSource: StatusesDataSource
) {
    authRouting(hashingService, userDataSource, jwtTokenService, jwtTokenConfig, refreshTokenConfig, refreshTokenService, tokenDataSource)
    dbRouting(
        orderingDataSource = orderingDataSource,
        popularItemsDataSource = popularItemsDataSource,
        recipientDataSource = recipientDataSource,
        addressDataSource = addressDataSource,
        faqDataSource = faqDataSource,
        statusesDataSource = statusesDataSource,
        orderStatusesDataSource =  orderStatusesDataSource
    )
}
