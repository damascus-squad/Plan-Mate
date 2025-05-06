package ui.views

import logic.model.User
import logic.usecase.auth.CreateMateUseCase
import ui.input.checkPasswordInput
import ui.input.checkUsernameInput

fun viewMateCreation(admin: User, createMateUseCase: CreateMateUseCase) {
    println("Creating new mate, please fill the following fields")

    val usernameInput: String = getUsernameInput()
    val passwordInput: String = getPasswordInput()

    createMateUseCase(admin = admin, newUsername = usernameInput, passwordInput)
}

fun getUsernameInput(): String {
    print("Enter username: ")
    var usernameInput: String = readln()
    while (!checkUsernameInput(usernameInput)) {
        print("enter username: ")
        usernameInput = readln()
    }
    return usernameInput
}

fun getPasswordInput(): String {
    print("Enter password: ")
    var passwordInput: String = readln()
    while (!checkPasswordInput(passwordInput)) {
        print("enter password: ")
        passwordInput = readln()
    }
    return passwordInput
}