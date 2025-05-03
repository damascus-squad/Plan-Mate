package org.damascus.logic.usecase.auth

import logic.model.User
import logic.repo.AuthenticationRepository

class AuthenticateUserLoginUseCase(
    private val authRepo: AuthenticationRepository
) {
    operator fun invoke(username: String, password: String): Result<User> {
        return runCatching { authRepo.login(username, password) }
    }
}