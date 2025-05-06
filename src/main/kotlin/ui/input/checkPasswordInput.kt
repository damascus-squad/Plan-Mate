package ui.input

private const val PASSWORD_PATTERN = "^[a-zA-Z0-9]+$"
private const val MINIMUM_Length = 6
private const val MAXIMUM_Length = 20
fun checkPasswordInput(password: String): Boolean {
    if (password.trim().isEmpty()) {
        print("empty field, ")
        return false
    }
    if (!password.first().toString().matches(Regex(PASSWORD_PATTERN))) {
        print("first letter should be letter or number, ")
        return false
    }
    if (password.last().isBlank()) {
        print("password should not end with space, ")
        return false
    }
    if (password.length < MINIMUM_Length) {
        print("Password is less than 6 characters, ")
        return false
    }
    if (password.length > MAXIMUM_Length) {
        print("Password is more than 20 characters, ")
        return false
    }

    return true
}

fun Char.isBlank(): Boolean {
    return this == ' ' // You can extend this to check for other whitespace characters if needed
}