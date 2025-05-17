package org.damascus.ui.views.user

import org.damascus.logic.model.User
import org.damascus.logic.usecase.auth.CreateMateUseCase
import org.damascus.ui.input.checkPasswordInput
import org.damascus.ui.input.checkUsernameInput
import org.koin.core.annotation.Single

@Single
class CreateMateUi(
    private val createMateUseCase: CreateMateUseCase
) {

    operator suspend fun invoke(admin: User) {
        println("Creating new mate, please fill the following fields")

        val usernameInput = getUsernameInput()
        val passwordInput = getPasswordInput()

        createMateUseCase(admin = admin, newUsername = usernameInput, newPassword = passwordInput)
    }

    private fun getUsernameInput(): String {
        print("Enter username: ")
        var input = readln()
        while (!checkUsernameInput(input)) {
            print("Enter valid username: ")
            input = readln()
        }
        return input
    }

    private fun getPasswordInput(): String {
        print("Enter password: ")
        var input = readln()
        while (!checkPasswordInput(input)) {
            print("Enter valid password: ")
            input = readln()
        }
        return input
    }
}