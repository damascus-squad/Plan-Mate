package ui.views

import logic.model.User
import logic.usecase.auth.AuthenticateUserLoginUseCase
import ui.io.Display
import ui.io.InputReader

class LoginView(
    private val authenticateUserLoginUseCase: AuthenticateUserLoginUseCase,
    private val inputReader: InputReader,
    private val display: Display
) {
    fun getLoggedUser(): User {
        while (true) {
            display.write(prompt = "🔐 Welcome to Plan Mate Login")
            val usernameInput = inputReader.readString(prompt = "👤 Enter your name ")
            val passwordInput = inputReader.readString(prompt = "🔒 Enter Your Password ")

            val userResult = authenticateUserLoginUseCase(
                username = usernameInput,
                password = passwordInput
            ).getOrNull()

            if (userResult != null) return userResult
            else println("\nIncorrect username or password, please try again.\n")
        }
    }
}