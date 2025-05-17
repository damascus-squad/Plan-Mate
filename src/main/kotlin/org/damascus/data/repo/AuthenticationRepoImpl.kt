package org.damascus.data.repo

import org.damascus.data.dto.UserDTO
import org.damascus.data.mapper.toModel
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

    override suspend fun login(username: String, password: String): User {
        val searchedUser = getUserByUsername(username) ?: throw UserNotFoundException()
        val searchedUserPassword = searchedUser.hashedPassword

        if (!hashingService.verifyData(password, searchedUserPassword)) {
            throw InvalidCredentialsException()
        }

        return searchedUser.toModel()
    }

    override suspend fun createMate(requester: User, newUsername: String, rawPassword: String): User {
        if (requester.userRole == UserRole.MATE) {
            throw UnauthorizedActionException("create a mate")
        }

        if (getUserByUsername(newUsername) != null) {
            throw UserAlreadyExistException(newUsername)
        }

        val hashedPassword = hashingService.hashData(rawPassword)
        val newMate = UserDTO(
            id = UUID.randomUUID(),
            username = newUsername,
            hashedPassword = hashedPassword,
            userRole = UserRole.MATE
        )
        usersDataSource.write(newMate)

        return newMate.toModel()
    }

    override suspend fun getMateById(userId: UUID): User {
        return usersDataSource.read()
            .find { it.id == userId }?.toModel() ?: throw UserNotFoundException()
    }

    override suspend fun getAllMates(): List<User> {
        return usersDataSource.read()
            .filter { it.userRole == UserRole.MATE }.map { it.toModel() }
    }

    private suspend fun getUserByUsername(username: String): UserDTO? {
        return usersDataSource.read().find { it.username == username }
    }
}