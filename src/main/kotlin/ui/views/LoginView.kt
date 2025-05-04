package org.damascus.ui.views

import logic.model.User
import org.damascus.logic.usecase.auth.AuthenticateUserLoginUseCase
import org.damascus.ui.io.InputReader

class LoginView(
    private val authenticateUserLoginUseCase: AuthenticateUserLoginUseCase,
    private val inputReader: InputReader
) {
    fun getLoggedUser(): User {
        while (true) {
            val usernameInput = inputReader.readString("Username:")
            val passwordInput = inputReader.readString("Password:")

            val userResult = authenticateUserLoginUseCase(
                username = usernameInput,
                password = passwordInput
            ).getOrNull()

            if (userResult != null) return userResult
            else println("\nIncorrect username or password, please try again.\n")
        }
    }
}