package logic.useCase.mate

import logic.model.Admin
import logic.model.Mate
import logic.model.User
import org.damascus.logic.exception.*

class CreateMateUseCase(
    private val authRepository: AuthenticationRepository,
    private val hashingService: MD5HashingService
) {

    operator fun invoke(requester: User, username: String, password: String): Mate {
        validateRequester(requester)
        validateCredentials(username, password)

        return authRepository.createMate(
            requester = requester,
            newUsername = username.trim(),
            rawPassword = hashingService.hashData(password)
        )
    }

    private fun validateRequester(requester: User) {
        if (requester !is Admin) {
            throw UnauthorizedActionException("create mates")
        }
    }

    private fun validateCredentials(username: String, password: String) {
        validateUsername(username.trim())
        validatePassword(password)
    }

    private fun validateUsername(username: String) {
        when {
            username.isBlank() ->
                throw BlankInputException("Username cannot be blank")

            username.contains(" ") ->
                throw InvalidUserNameInputException("Username cannot contain spaces")

            authRepository.findByUsername(username) != null ->
                throw UserAlreadyExistException(username)
        }
    }

    private fun validatePassword(password: String) {
        when {
            password.isBlank() ->
                throw BlankInputException("Password cannot be blank")

            isAlreadyHashed(password) ->
                throw InvalidPasswordException("Password must be in raw format")
        }
    }

    private fun isAlreadyHashed(password: String): Boolean {
        return password.length == 32 && password.matches(Regex("^[a-f0-9]{32}$"))
    }
}