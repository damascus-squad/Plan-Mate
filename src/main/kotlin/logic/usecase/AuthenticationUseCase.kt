package org.damascus.logic.usecase

import logic.model.Admin
import logic.model.Mate
import logic.repo.AuthenticationRepository

class AuthenticationUseCase(
    private val authRepo: AuthenticationRepository
) {
    fun createMate(admin: Admin, newUsername: String, newPassword: String): Result<Mate> {
        return runCatching { authRepo.createMate(admin, newUsername, newPassword) }
    }
}

