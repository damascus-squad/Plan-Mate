package org.damascus.data

import User
import model.Admin
import model.Mate
import org.damascus.logic.HashingService
import org.damascus.logic.exception.InvalidPasswordException
import org.damascus.logic.exception.UnauthorizedActionException
import org.damascus.logic.exception.UserAlreadyExistException
import org.damascus.logic.exception.UserNotFoundException
import org.damascus.logic.AuthenticationRepository
import java.util.*

class AuthenticationRepoImpl(
    private val hashingService: HashingService,
    val users: MutableList<User>
) : AuthenticationRepository {

    override fun login(username: String, password: String): User {
        val searchedUser = findByUsername(username) ?: throw UserNotFoundException(username)

        if (!hashingService.verifyData(password, searchedUser.password)) {
            throw InvalidPasswordException(password)
        }

        return searchedUser
    }

    override fun createMate(requester: User, newUsername: String, rawPassword: String): Mate {
        if (requester !is Admin) {
            throw UnauthorizedActionException("create a mate")
        }
        if (findByUsername(newUsername) != null) {
            throw UserAlreadyExistException(newUsername)
        }

        val hashedPassword = hashingService.hashData(rawPassword)
        val newMate = Mate(id = UUID.randomUUID(), newUsername, hashedPassword)
        users.add(newMate)

        return newMate
    }

    override fun createAdmin(requester: User, newUsername: String, rawPassword: String): Admin {
        if (requester !is Admin) {
            throw UnauthorizedActionException("create an admin")
        }
        if (findByUsername(newUsername) != null) {
            throw UserAlreadyExistException(newUsername)
        }

        val hashedPassword = hashingService.hashData(rawPassword)
        val newAdmin = Admin(id = UUID.randomUUID(), newUsername, hashedPassword)
        users.add(newAdmin)

        return newAdmin
    }


    override fun findByUsername(username: String): User? {
        return users.find { it.username == username }
    }
}
