package logic

import java.util.*

class TaskAlreadyExistsException(taskId: UUID) : Exception("Task with ID $taskId already exists.")
class TaskNotFoundException(taskId: UUID) : Exception("Task with ID $taskId not found.")