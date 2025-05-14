package ui.views

import logic.model.User
import logic.usecase.auth.AuthenticateUserLoginUseCase
import ui.io.InputReader

class LoginView(
    private val authenticateUserLoginUseCase: AuthenticateUserLoginUseCase,
    private val inputReader: InputReader
) {
    fun getLoggedUser(): User {
        while (true) {
            val usernameInput = inputReader.readString("Username:")
            if (usernameInput.isBlank()) {
                println("\nUsername cannot be empty, please try again.\n")
                continue
            }

            val passwordInput = inputReader.readString("Password:")
            if (passwordInput.isBlank()) {
                println("\nPassword cannot be empty, please try again.\n")
                continue
            }

            val userResult = authenticateUserLoginUseCase(
                username = usernameInput,
                password = passwordInput
            ).getOrNull()

            if (userResult != null) return userResult
            else println("\nIncorrect username or password, please try again.\n")
        }
    }
}