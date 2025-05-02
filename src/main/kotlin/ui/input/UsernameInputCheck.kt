package org.damascus.ui.input

val TEST_USERNAMES: List<String> = listOf("harith", "yosuf", "omar", "ali")

fun usernameInputCheck(username: String): Boolean {
    if(username.isEmpty()) {
        print("empty field, ")
        return false
    }
    if(username.length < 4) {
        print("should not be less than 4 characters, ")
        return false
    }
    if(username.length > 20) {
        print("should not be more than 20 characters, ")
        return false
    }
    if(!username.matches(Regex("^[a-zA-Z0-9_]*$"))) {
        print("has not allowed prefix, ")
        return false
    }
    else if (!username.first().isLetter()) {
        print("First letter should be letter, ")
        return false
    }
    TEST_USERNAMES.forEach { it ->
        if(username.lowercase() == it.lowercase()) {
            print("username is used, ")
            return false
        }
    }
    return true
}
