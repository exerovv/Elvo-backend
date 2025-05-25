package com.example.database.user


import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class UserDataSourceImpl : UserDataSource {
    override suspend fun getUserById(id: Int): User? {
        return transaction {
            UserTable
                .selectAll()
                .where { UserTable.userId eq id }
                .map {
                    User(
                        userId = it[UserTable.userId],
                        username = it[UserTable.username],
                        password = it[UserTable.password],
                    )
                }
                .firstOrNull()
        }
    }

    override suspend fun insertUser(user: User) : Boolean {
        try{
            transaction {
                UserTable.insert {
                    it[userId] = user.userId
                    it[username] = user.username
                    it[password] = user.password

                }
            }
            return true
        }catch (_ : Exception){
            return false
        }
    }

    override suspend fun userExists(id: Int): Boolean {
        val userExists = transaction {
            UserTable
                .selectAll()
                .where { UserTable.userId eq id}
                .firstOrNull()
        }
        return userExists != null
    }
}
