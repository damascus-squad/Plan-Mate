package org.damascus.logic.repo

import org.damascus.logic.model.User
import java.util.*

interface AuthenticationRepository {
    fun login(username: String, password: String): User
    fun createMate(requester: User, newUsername: String, rawPassword: String): User
    fun getMateById(userId: UUID): User?
    fun getAllMates(): List<User>
}