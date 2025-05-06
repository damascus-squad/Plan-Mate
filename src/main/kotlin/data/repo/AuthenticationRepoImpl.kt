package org.damascus.data.repo

import logic.exception.InvalidCredentialsException
import logic.exception.UnauthorizedActionException
import logic.exception.UserAlreadyExistException
import logic.exception.UserNotFoundException
import logic.model.User
import logic.repo.AuthenticationRepository
import logic.repo.DataSource
import data.dto.UserDTO
import org.damascus.logic.model.UserRole
import org.damascus.logic.service.HashingService
import java.util.*
import kotlin.collections.find

class AuthenticationRepoImpl(
    private val hashingService: HashingService,
    val usersDataSource: DataSource<UserDTO>
) : AuthenticationRepository {

    override fun login(username: String, password: String): User {
        val searchedUser = getUserByUsername(username) ?: throw UserNotFoundException(username)
        val searchedUserPassword = searchedUser.hashedPassword

        if (!hashingService.verifyData(password, searchedUserPassword)) {
            throw InvalidCredentialsException()
        }

        return searchedUser.toUser()
    }

    override fun createMate(requester: User, newUsername: String, rawPassword: String): User {
        if (requester.userRole == UserRole.MATE) {
            throw UnauthorizedActionException("create a mate")
        }

        if (getUserByUsername(newUsername) != null) {
            throw UserAlreadyExistException(newUsername)
        }

        val hashedPassword = hashingService.hashData(rawPassword)
        val newMate = UserDTO(id = UUID.randomUUID(), hashedPassword, newUsername, UserRole.MATE)
        usersDataSource.write(newMate)

        return newMate.toUser()
    }

    private fun getUserByUsername(username: String): UserDTO? {
        return usersDataSource.read().find { it.username == username }
    }
}