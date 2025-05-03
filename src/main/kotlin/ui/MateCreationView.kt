package org.damascus.ui

import logic.model.Admin
import logic.model.Mate
import org.damascus.data.authentication.AuthenticationRepoImpl
import org.damascus.logic.model.Role
import org.damascus.logic.usecase.AuthenticationUseCase
import org.damascus.ui.input.checkPasswordInput
import org.damascus.ui.input.checkUsernameInput
import java.util.UUID

fun viewMateCreation(admin: Admin, authenticationUseCase: AuthenticationUseCase) {
    println("Creating new mate, please fill the following fields")

    val usernameInput: String = getUsernameInput()
    val passwordInput: String = getPasswordInput()

    authenticationUseCase.createMate(admin = admin, newUsername = usernameInput, passwordInput)
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