package org.damascus.data.repo

import org.damascus.data.dto.UserDTO
import org.damascus.logic.exception.InvalidCredentialsException
import org.damascus.logic.exception.UnauthorizedActionException
import org.damascus.logic.exception.UserAlreadyExistException
import org.damascus.logic.exception.UserNotFoundException
import org.damascus.logic.model.User
import org.damascus.logic.model.UserRole
import org.damascus.logic.repo.AuthenticationRepository
import org.damascus.logic.repo.DataSource
import org.damascus.logic.service.HashingService
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import java.util.*

@Single
class AuthenticationRepoImpl(
    private val hashingService: HashingService,
    @Named("userDataSource")
    private val usersDataSource: DataSource<UserDTO>
    ) : AuthenticationRepository {

    override fun login(username: String, password: String): User {
        val searchedUser = getUserByUsername(username) ?: throw UserNotFoundException()
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

    override fun getMateById(userId: UUID): User {
        return usersDataSource.read()
            .find { it.id == userId }?.toUser() ?: throw UserNotFoundException()
    }

    override fun getAllMates(): List<User>{
        return usersDataSource.read()
            .filter { it.userRole == UserRole.MATE }.map { it.toUser() }
    }

    private fun getUserByUsername(username: String): UserDTO? {
        return usersDataSource.read().find { it.username == username }
    }
}