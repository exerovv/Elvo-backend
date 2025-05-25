package com.example.database.user


import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class UserDataSourceImpl : UserDataSource {
    override suspend fun getUserByUsername(username: String): UserDTO? {
        return transaction {
            UserTable
                .selectAll()
                .where { UserTable.username eq username }
                .map {
                    UserDTO(
                        userId = it[UserTable.id].value,
                        user = User(
                            username = it[UserTable.username],
                            password = it[UserTable.password],
                        )
                    )
                }
                .firstOrNull()
        }
    }

    override suspend fun insertUser(user: User) : Int? {
        return try{
            transaction {
                UserTable.insertAndGetId {
                    it[username] = user.username
                    it[password] = user.password
                }
            }.value
        }catch (_ : Exception){
            null
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
