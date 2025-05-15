package org.damascus.logic.usecase.auth

import org.damascus.logic.exception.NoMatesAvailableException
import org.damascus.logic.exception.UserNotFoundException
import org.damascus.logic.model.User
import org.damascus.logic.repo.AuthenticationRepository
import org.koin.core.annotation.Single
import java.util.*

@Single
class ManageMateUseCase(
    private val authRepo: AuthenticationRepository
) {
    fun getMate(userId: UUID) = authRepo.getMateById(userId) ?: throw UserNotFoundException()
    fun getAllMates(): List<User> = authRepo.getAllMates().also { if (it.isEmpty()) throw NoMatesAvailableException() }
}
