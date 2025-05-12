package org.damascus.ui.views.user

import logic.model.User
import logic.usecase.auth.CreateMateUseCase
import ui.input.checkPasswordInput
import ui.input.checkUsernameInput

class CreateMateUi(
    private val createMateUseCase: CreateMateUseCase
) {

    operator fun invoke(admin: User) {
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