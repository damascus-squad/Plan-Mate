package org.damascus.logic.repository

import logic.model.Mate
import logic.model.User

interface AuthenticationRepository {
    fun login(username: String, password: String): User
    fun createMate(requester: User, newUsername: String, rawPassword: String): Mate
    fun getUserByUsername(username: String): User?
}