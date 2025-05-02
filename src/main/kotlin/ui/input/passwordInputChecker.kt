package org.damascus.ui.input

fun passwordInputChecker(password: String): Boolean {
    if(password.trim().isEmpty()) {
        print("empty field, ")
        return false
    }
    if(!password[0].toString().matches(Regex("^[a-zA-Z0-9]*$"))) {
        print("first letter should be letter or number, ")
        return false
    }
    if(password.last() == ' ') {
        print("password should not end with space, ")
        return false
    }
    if(password.length < 6) {
        print("Password is less than 6 characters, ")
        return false
    }
    if(password.length > 16) {
        print("Password is more than 20 characters, ")
        return false
    }

    return true
}