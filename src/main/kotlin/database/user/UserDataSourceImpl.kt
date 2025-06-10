package com.example.database.user


import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class UserDataSourceImpl : UserDataSource {
    override suspend fun getUserByUsername(username: String): UserDTO? = newSuspendedTransaction {
        UserTable
            .selectAll()
            .where { UserTable.username eq username }
            .map {
                UserDTO(
                    userId = it[UserTable.id].value,
                    User(
                        username = it[UserTable.username],
                        password = it[UserTable.password]
                    )
                )
            }
            .firstOrNull()
    }

    override suspend fun insertUser(user: User): Int = newSuspendedTransaction {
        UserTable.insertAndGetId {
            it[username] = user.username
            it[password] = user.password
        }.value
    }

    override suspend fun getUserInfoById(userId: Int): UserInfo? = newSuspendedTransaction {
        UserTable
            .selectAll()
            .where { UserTable.id eq userId }
            .map {
                UserInfo(
                    userId = userId,
                    username = it[UserTable.username],
                    avatarUrl = it[UserTable.avatar_url]
                )
            }.firstOrNull()
    }
}
