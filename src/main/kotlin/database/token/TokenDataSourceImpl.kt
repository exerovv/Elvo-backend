package com.example.database.token

import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import kotlinx.datetime.Clock

class TokenDataSourceImpl : TokenDataSource {
    override suspend fun getTokenById(id: Int): Token? {
        return transaction {
            TokenTable
                .selectAll()
                .where { TokenTable.id eq id }
                .map {
                    Token(
                        id = it[TokenTable.id],
                        userId = it[TokenTable.userId],
                        refreshToken = it[TokenTable.refreshToken],
                        revoked = it[TokenTable.revoked]
                    )
                }
                .firstOrNull()
        }
    }

    override suspend fun insertToken(token: Token) : Boolean {
        try{
            transaction {
                TokenTable.insert {
                    it[id] = token.id
                    it[userId] = token.userId
                    it[refreshToken] = token.refreshToken
                    it[issuedAt] = Clock.System.now()
                    it[expiresAt] = Clock.System.now()
                    it[revoked] = token.revoked

                }
            }
            return true
        }catch (_ : Exception){
            return false
        }
    }

    override suspend fun tokenExists(id: Int): Boolean {
        val tokenExists = transaction {
            TokenTable
                .selectAll()
                .where { TokenTable.id eq id}
                .firstOrNull()
        }
        return tokenExists != null
    }
}