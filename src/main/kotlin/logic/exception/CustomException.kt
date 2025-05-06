package logic.exception

import java.util.*

class TaskAlreadyExistsException(taskId: UUID) : Exception("Task with ID $taskId already exists.")

class TaskNotFoundException(taskId: UUID) : Exception("Task with ID $taskId not found.")

class UserNotFoundException(username: String) : Exception("User '$username' not found")

class InvalidCredentialsException : Exception("Invalid username or password ")

class UnauthorizedActionException(action: String) : Exception("You are not allowed to $action")

class UserAlreadyExistException(newUsername: String) : Exception("Username '$newUsername' already exists.")

class DuplicateStateException(name: String) : Exception("A state with ID $name already exists.")

class StateNotFoundException() : Exception("State not found.")

class NoLogException(message: String) : Exception(message)

class ProjectsNotAvailableException(message: String) : Exception(message)

class NoTasksFoundException(message: String) : Exception(message)