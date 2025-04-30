package org.damascus.logic.services

import User
import model.Admin
import model.Mate

interface AuthenticationRepository {
    fun login(user: User): User
    fun createMate(requester: User, newUsername: String, rawPassword: String): Mate
    fun createAdmin(requester: User, newUsername: String, rawPassword: String): Admin
    fun findByUsername(username: String): User?
}
