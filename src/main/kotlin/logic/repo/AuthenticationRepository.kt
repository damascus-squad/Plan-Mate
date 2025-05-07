package logic.repo

import logic.model.User

interface AuthenticationRepository {
    fun login(username: String, password: String): User
    fun createMate(requester: User, newUsername: String, rawPassword: String): User
    fun getAllMates():List<User>
}