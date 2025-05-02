package org.damascus.logic.exception

class UserNotFoundException(username: String) : Exception("User '$username' not found")

class InvalidCredentialsException : Exception("Invalid username or password ")

class UnauthorizedActionException(action: String) : Exception("You are not allowed to $action")

class UserAlreadyExistException(newUsername: String) : Exception("Username '$newUsername' already exists.")
