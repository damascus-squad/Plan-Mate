package org.damascus.logic.usecase.auth

import logic.exception.NoMatesAvailableException
import logic.model.User
import logic.repo.AuthenticationRepository

class GetAllMatesUseCase(private val authRepo: AuthenticationRepository) {
    operator fun invoke(): List<User> {
        val mates = authRepo.getAllMates()
        if (mates.isEmpty()) throw NoMatesAvailableException()
        return mates
    }
}