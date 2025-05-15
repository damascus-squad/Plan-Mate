package org.damascus.logic.usecase.auth

import org.damascus.logic.exception.NoMatesAvailableException
import org.damascus.logic.exception.UserNotFoundException
import org.damascus.logic.model.User
import org.damascus.logic.repo.AuthenticationRepository
import java.util.*

class ManageMateUseCase(
    private val authRepo: AuthenticationRepository
) {
    suspend fun getMate(userId: UUID) = authRepo.getMateById(userId) ?: throw UserNotFoundException()
    suspend fun getAllMates(): List<User> =
        authRepo.getAllMates().also { if (it.isEmpty()) throw NoMatesAvailableException() }
}
