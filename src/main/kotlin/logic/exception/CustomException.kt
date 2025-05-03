package org.damascus.logic.exception

class UserNotFoundException(username: String) : Exception("User '$username' not found")

class InvalidPasswordException(password: String) : Exception("Invalid password '$password'")

class UnauthorizedActionException(action: String) : Exception("You are not allowed to $action")
class InvalidCredentialsException : Exception("Invalid username or password ")
class UserAlreadyExistException(newUsername: String) : Exception("Username '$newUsername' already exists.")
class InvalidUserNameInputException(message: String) : RuntimeException(message)
class BlankInputException(message: String = "Input cannot be blank") : RuntimeException(message)
