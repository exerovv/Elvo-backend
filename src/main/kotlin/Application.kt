package com.example

import com.example.authentication.configureSecurity
import com.example.database.address.AddressDataSourceImpl
import com.example.database.configureDatabases
import com.example.database.ordering.OrderingDataSourceImpl
import com.example.database.popularitems.PopularItemsDataSourceImpl
import com.example.database.recipient.RecipientDataSourceImpl
import com.example.database.token.TokenDataSourceImpl
import com.example.database.user.UserDataSourceImpl
import com.example.security.hashing.BcryptHashingService
import com.example.security.token.JwtTokenService
import com.example.security.token.JWTTokenConfig
import com.example.security.token.RefreshTokenConfig
import com.example.security.token.RefreshTokenService
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val jwt = environment.config.config("ktor.jwt")
    val jwtTokenService = JwtTokenService()
    val refreshTokenService = RefreshTokenService()
    val jwtTokenConfig = JWTTokenConfig(
        issuer = jwt.property("issuer").getString(),
        audience = jwt.property("audience").getString(),
        expiresIn = jwt.property("expiry").getString().toLong(),
        secret = jwt.property("secret").getString(),
    )
    val refreshTokenConfig = RefreshTokenConfig(
        90000,
        32
    )
    val hashingService = BcryptHashingService()
    val userDataSource = UserDataSourceImpl()
    val tokenDataSource = TokenDataSourceImpl()
    val orderingDataSource = OrderingDataSourceImpl()
    val popularItemsDataSource = PopularItemsDataSourceImpl()
    val recipientDataSource = RecipientDataSourceImpl()
    val addressDataSource = AddressDataSourceImpl()
    configureSerialization()
    configureDatabases()
    configureSecurity(jwtTokenConfig)
    configureRouting(
        userDataSource,
        hashingService,
        jwtTokenService,
        jwtTokenConfig,
        refreshTokenConfig,
        refreshTokenService,
        tokenDataSource,
        orderingDataSource,
        popularItemsDataSource,
        recipientDataSource,
        addressDataSource
    )
}
