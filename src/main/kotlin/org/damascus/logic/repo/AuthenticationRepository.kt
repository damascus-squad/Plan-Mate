package org.damascus.logic.repo

import org.damascus.logic.model.User
import java.util.*

interface AuthenticationRepository {
    suspend fun login(username: String, password: String): User
    suspend fun createMate(requester: User, newUsername: String, rawPassword: String): User
    suspend fun getMateById(userId: UUID): User?
    suspend fun getAllMates(): List<User>
}