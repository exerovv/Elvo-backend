package com.example.database.token

import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.plus
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.Date
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class TokenDataSourceImpl : TokenDataSource {
//    override suspend fun getTokenById(id: Int): Token? {
//        return newSuspendedTransaction {
//            TokenTable
//                .selectAll()
//                .where { TokenTable.id eq id }
//                .map {
//                    Token(
//                        userId = it[TokenTable.userId],
//                        refreshToken = it[TokenTable.refreshToken],
//                        expiration = it[TokenTable.expiresAt],
//                        revoked = it[TokenTable.revoked]
//                    )
//                }
//                .firstOrNull()
//        }
//    }

    override suspend fun insertToken(token: Token): Boolean {
        try{
            newSuspendedTransaction {
                TokenTable.insert {
                    it[userId] = token.userId
                    it[refreshToken] = token.refreshToken
                    it[issuedAt] = Clock.System.now()
                    it[expiresAt] = Clock.System.now().plus(token.expiration.milliseconds)
                    it[revoked] = token.revoked
                }
            }
            return true
        } catch (_: Exception) {
            return false
        }
    }

//    override suspend fun tokenExists(id: Int): Boolean {
//        val tokenExists = newSuspendedTransaction {
//            TokenTable
//                .selectAll()
//                .where { TokenTable.id eq id}
//                .firstOrNull()
//        }
//        return tokenExists != null
//    }
}