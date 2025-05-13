package logic.repo

import logic.model.User
import java.util.UUID

interface AuthenticationRepository {
    fun login(username: String, password: String): User
    fun createMate(requester: User, newUsername: String, rawPassword: String): User
    fun getMateById(userId: UUID): User?
    fun getAllMates(): List<User>
}