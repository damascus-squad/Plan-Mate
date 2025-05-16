package org.damascus.ui.views.user

import org.damascus.logic.model.User
import org.damascus.logic.usecase.auth.AuthenticateUserLoginUseCase
import org.damascus.ui.io.Display
import org.damascus.ui.io.InputReader
import org.koin.core.annotation.Single

@Single
class LoginView(
    private val authenticateUserLoginUseCase: AuthenticateUserLoginUseCase,
    private val inputReader: InputReader,
    private val display: Display
) {
    fun getLoggedUser(): User {
        while (true) {
            display.write(prompt = "🔐 Welcome to Plan Mate Login")
            val usernameInput = inputReader.readString(prompt = "👤 Enter your name ")

            if (usernameInput.isBlank()) {
                println("\nUsername cannot be empty, please try again.\n")
                continue
            }

            val passwordInput = inputReader.readString(prompt = "🔒 Enter Your Password ")

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