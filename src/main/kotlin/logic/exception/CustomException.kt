package org.damascus.logic.exception

import java.util.*

class TaskAlreadyExistsException(taskId: UUID) : Exception("Task with ID $taskId already exists.")
class TaskNotFoundException(taskId: UUID) : Exception("Task with ID $taskId not found.")
class UserNotFoundException(username: String) : Exception("User '$username' not found")
class InvalidPasswordException(password: String) : Exception("Invalid password '$password'")
class UnauthorizedActionException(action: String) : Exception("You are not allowed to $action")
class InvalidCredentialsException : Exception("Invalid username or password ")
class UserAlreadyExistException(newUsername: String) : Exception("Username '$newUsername' already exists.")
class DuplicateStateException(id: UUID) : Exception("A state with ID ${id} already exists.")
class StateNotFoundException(id: UUID) : Exception("State with id ${id} not found.")
class InvalidStateException(message: String) : Exception(message)
class NoHistoryException(message: String) : Exception(message)
class InvalidUserNameInputException(message: String) : RuntimeException(message)
class BlankInputException(message: String = "Input cannot be blank") : RuntimeException(message)
