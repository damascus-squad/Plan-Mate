package org.damascus.logic.usecase.auth

import logic.exception.NoMatesAvailableException
import logic.exception.UserNotFoundException
import logic.model.User
import logic.repo.AuthenticationRepository
import java.util.*

class ManageMateUseCase(
    private val authRepo: AuthenticationRepository
) {
    fun getMate(userId: UUID) = authRepo.getMateById(userId) ?: throw UserNotFoundException()
    fun getAllMates(): List<User> = authRepo.getAllMates().also { if (it.isEmpty()) throw NoMatesAvailableException() }
}
