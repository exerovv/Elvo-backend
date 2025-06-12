package com.example.database.token

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update

class TokenDataSourceImpl: TokenDataSource {
    override suspend fun insertToken(token: Token): Int = newSuspendedTransaction {
        TokenTable.insertAndGetId {
            it[userId] = token.userId
            it[refreshToken] = token.refreshToken
            it[issuedAt] = token.issuedAt
            it[expiresAt] = token.expiresAt
            it[revoked] = token.revoked
        }.value
    }

    override suspend fun findToken(userId: Int): Token? {
        return newSuspendedTransaction {
            TokenTable
                .selectAll()
                .where(TokenTable.userId eq userId)
                .map {
                    Token(
                        it[TokenTable.userId],
                        it[TokenTable.refreshToken],
                        it[TokenTable.issuedAt],
                        it[TokenTable.expiresAt],
                        it[TokenTable.revoked]
                    )
                }
                .firstOrNull()
        }
    }



    override suspend fun updateToken(token: Token): Boolean {
        return newSuspendedTransaction {
            TokenTable.update({
                TokenTable.userId eq token.userId
            }){
                it[TokenTable.refreshToken] = token.refreshToken
                it[issuedAt] = token.issuedAt
                it[TokenTable.expiresAt] = token.expiresAt
            } > 0
        }
    }
}