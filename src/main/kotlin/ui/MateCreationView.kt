package org.damascus.ui

import model.Mate
import org.damascus.ui.input.passwordInputChecker
import org.damascus.ui.input.usernameInputCheck
import java.util.UUID

fun mateCreationView() {
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

fun getUsernameInput() : String {
    print("Enter username: ")
    var usernameInput: String = readln()
    while (!usernameInputCheck(usernameInput)){
        print("enter username: ")
        usernameInput = readln()
    }
    return usernameInput
}

fun getPasswordInput(): String {
    print("Enter password: ")
    var passwordInput: String = readln()
    while (!passwordInputChecker(passwordInput)){
        print("enter password: ")
        passwordInput = readln()
    }
    return passwordInput
}