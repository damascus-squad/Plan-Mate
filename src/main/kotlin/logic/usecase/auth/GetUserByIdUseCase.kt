package org.damascus.logic.usecase.auth

import logic.exception.UserNotExistException
import logic.model.User
import logic.repo.AuthenticationRepository
import java.util.UUID

class GetUserByIdUseCase(private val authRepo: AuthenticationRepository) {
    operator fun invoke(userId:UUID): User {
        val mate = authRepo.getMateById(userId) ?: throw UserNotExistException()
        return mate
    }
}