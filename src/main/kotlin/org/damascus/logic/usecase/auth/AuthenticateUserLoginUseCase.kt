package org.damascus.logic.usecase.auth

import org.damascus.logic.model.User
import org.damascus.logic.repo.AuthenticationRepository

class AuthenticateUserLoginUseCase(
    private val authRepo: AuthenticationRepository
) {
    operator fun invoke(username: String, password: String): Result<User> {
        return runCatching { authRepo.login(username, password) }
    }
}