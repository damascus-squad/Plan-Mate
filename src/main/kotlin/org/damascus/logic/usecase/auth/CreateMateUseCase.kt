package org.damascus.logic.usecase.auth

import org.damascus.logic.model.User
import org.damascus.logic.repo.AuthenticationRepository

class CreateMateUseCase(
    private val authRepo: AuthenticationRepository
) {
    suspend operator fun invoke(admin: User, newUsername: String, newPassword: String): Result<User> {
        return runCatching { authRepo.createMate(admin, newUsername, newPassword) }
    }
}