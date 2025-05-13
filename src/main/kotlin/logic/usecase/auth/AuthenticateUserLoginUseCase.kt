package logic.usecase.auth

import logic.model.User
import logic.repo.AuthenticationRepository

class AuthenticateUserLoginUseCase(
    private val authRepo: AuthenticationRepository
) {
    operator fun invoke(username: String, password: String): Result<User> {
        if (username.isBlank()) {
            return Result.failure(IllegalArgumentException("Username cannot be empty"))
        }
        if (password.isBlank()) {
            return Result.failure(IllegalArgumentException("Password cannot be empty"))
        }
        if (!username.matches(Regex("^[A-Za-z0-9+_.-]+@(.+)$"))) {
            return Result.failure(IllegalArgumentException("Invalid email format"))
        }
        return runCatching { authRepo.login(username, password) }
    }
}