package org.damascus.ui.input

val TEST_USERNAMES: List<String> = listOf("harith", "yosuf", "omar", "ali")
private const val USERNAME_PATTERN = "^[a-zA-Z0-9_]*$"
private const val MINIMUM_Length = 4
private const val MAXIMUM_Length = 20

fun checkUsernameInput(username: String): Boolean {
    if(username.isEmpty()) {
        print("empty field, ")
        return false
    }
    if(username.length < MINIMUM_Length) {
        print("should not be less than 4 characters, ")
        return false
    }
    if(username.length > MAXIMUM_Length) {
        print("should not be more than 20 characters, ")
        return false
    }
    if(!username.matches(Regex(USERNAME_PATTERN))) {
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
