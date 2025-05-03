package org.damascus.logic.usecase

import logic.model.Admin
import logic.model.Mate
import logic.model.User
import org.damascus.logic.repository.AuthenticationRepository

class AuthenticationUseCase(
    private val authRepo: AuthenticationRepository
) {
    fun login(username: String, password: String): Result<User> {
        return runCatching { authRepo.login(username, password) }
    }

    fun createMate(admin: Admin, newUsername: String, newPassword: String): Result<Mate> {
        return runCatching { authRepo.createMate(admin, newUsername, newPassword) }
    }
}

