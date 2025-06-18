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
            it[UserTable.username] = user.username
            it[UserTable.password] = user.password
            it[UserTable.avatarUrl] = "https://sun9-2.userapi.com/impg/NvZm7CqNeuPqmqUH2cWmenSe0soMk0dLmTcGOQ/KvoKt4kpESU.jpg?size=1024x1024&quality=95&sign=aaee1fdc1db93f6da3eb0556f4b9566f&type=album"
        }.value
    }

    override suspend fun getUserInfoById(userId: Int): UserInfoResponse? = newSuspendedTransaction {
        UserTable
            .selectAll()
            .where { UserTable.id eq userId }
            .map {
                UserInfoResponse(
                    userId = userId,
                    username = it[UserTable.username],
                    avatarUrl = it[UserTable.avatarUrl]
                )
            }.firstOrNull()
    }
}
