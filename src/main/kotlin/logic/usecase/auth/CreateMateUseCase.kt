package logic.usecase.auth

import logic.model.Admin
import logic.model.Mate
import logic.repo.AuthenticationRepository

class CreateMateUseCase(
    private val authRepo: AuthenticationRepository
) {
    operator fun invoke(admin: Admin, newUsername: String, newPassword: String): Result<Mate> {
        return runCatching { authRepo.createMate(admin, newUsername, newPassword) }
    }
}