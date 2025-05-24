package com.example.database.user

import com.example.database.models.User
import com.example.database.models.UserDataSource
import com.example.database.models.UserTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class UserDataSourceImpl : UserDataSource {
    override suspend fun getUserByUsername(username: String): User? {
        return transaction {
            UserTable
                .selectAll()
                .where { UserTable.username eq username }
                .map {
                    User(
                        username = it[UserTable.username],
                        password = it[UserTable.password],
                        salt = it[UserTable.salt]
                    )
                }
                .firstOrNull()
        }
    }

    override suspend fun insertUser(user: User) : Boolean {
        try{
            transaction {
                UserTable.insert {
                    it[username] = user.username
                    it[password] = user.password
                    it[salt] = user.salt
                }
            }
            return true
        }catch (_ : Exception){
            return false
        }
    }

    override suspend fun userExists(username: String): Boolean {
        val userExists = transaction {
            UserTable
                .selectAll()
                .where { UserTable.username eq username}
                .firstOrNull()
        }
        return userExists != null
    }
}
