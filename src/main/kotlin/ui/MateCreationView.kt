package org.damascus.ui

import model.Mate
import org.damascus.ui.input.checkPasswordInput
import org.damascus.ui.input.checkUsernameInput
import java.util.UUID

fun viewMateCreation() {
    println("Creating new mate, please fill the following fields")

    val usernameInput: String = getUsernameInput()
    val passwordInput: String = getPasswordInput()

    val newMate = Mate(
        username = usernameInput,
        password = passwordInput,
        id = UUID.randomUUID(),
    )

    // TODO: add the new mate to data
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