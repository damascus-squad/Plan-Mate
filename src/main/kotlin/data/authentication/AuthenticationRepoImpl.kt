package org.damascus.data.authentication

import logic.model.Admin
import logic.model.Mate
import logic.model.User
import org.damascus.data.DataSource
import org.damascus.logic.HashingService
import org.damascus.logic.exception.InvalidPasswordException
import org.damascus.logic.exception.UnauthorizedActionException
import org.damascus.logic.exception.UserAlreadyExistException
import org.damascus.logic.exception.UserNotFoundException
import org.damascus.logic.AuthenticationRepository
import org.damascus.logic.model.Role
import java.util.*

class AuthenticationRepoImpl(
    private val hashingService: HashingService,
    val usersDataSource: DataSource<User>
) : AuthenticationRepository {

    override fun login(username: String, password: String): User {
        val searchedUser = getUserByUsername(username) ?: throw UserNotFoundException(username)

        if (!hashingService.verifyData(password, searchedUser.password)) {
            throw InvalidPasswordException(password)
        }

        return searchedUser
    }

    override fun createMate(requester: User, newUsername: String, rawPassword: String): Mate {
        if (requester !is Admin) {
            throw UnauthorizedActionException("create a mate")
        }
        if (getUserByUsername(newUsername) != null) {
            throw UserAlreadyExistException(newUsername)
        }

        val hashedPassword = hashingService.hashData(rawPassword)
        val newMate = Mate(id = UUID.randomUUID(), newUsername, hashedPassword, Role.MATE)
        usersDataSource.write(newMate)

        return newMate
    }

    override fun getUserByUsername(username: String): User? {
        return usersDataSource.read().find { it.username == username }
    }
}
