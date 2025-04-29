package org.damascus.logic.services

import org.damascus.model.Admin
import org.damascus.model.Mate
import org.damascus.model.User

interface AuthenticationRepository {
    fun login(user: User): User
    fun createMate(requester: User, newUsername: String, rawPassword: String): Mate
    fun createAdmin(requester: User, newUsername: String, rawPassword: String): Admin
    fun findByUsername(username: String): User?
}
