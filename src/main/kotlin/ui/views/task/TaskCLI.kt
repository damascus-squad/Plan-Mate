package ui.views.task

import logic.model.Task
import logic.repo.TaskStateRepository
import logic.usecase.task.DeleteTaskUseCase
import logic.usecase.task.GetTaskUseCase
import logic.usecase.task.UpdateTaskUseCase
import ui.io.Display
import ui.io.InputReader
import ui.util.UiAction
import java.util.*

class TaskCLI(
    private val display: Display,
    private val inputReader: InputReader,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val taskStateRepository: TaskStateRepository,
    private val getTaskUseCase: GetTaskUseCase,
) : TaskUIController {
    fun start(taskId: UUID) {
        viewTaskDetails(taskId)
        display.displayMenu(
            listOf(
                UiAction("Edit Task") { editTask(taskId) },
                UiAction("Delete Task") { deleteTask(taskId) }
            ),
            menuTitle = "Task Management"
        )
    }

    override fun editTask(taskId: UUID) {
        val updatedTask = getTaskUseCase(taskId)

        display.displayMenu(
            listOf(
                UiAction("Title") { updateField("Title", updatedTask) },
                UiAction("Description") { updateField("Description", updatedTask) },
                UiAction("Status") { updateField("Status", updatedTask) }
            ),
            menuTitle = "\nSelect the field you want to update:"
        )

        updateTaskUseCase(taskId, updatedTask)

        println("✅ Task updated successfully!")
    }

    override fun deleteTask(taskId: UUID) {
        val confirm = inputReader.readString("Are you sure you want to delete this task? (yes/no): ")
        if (confirm.equals("yes", ignoreCase = true)) {
            deleteTaskUseCase(taskId)
            println("🗑️ Task deleted successfully!")
        } else {
            println("❌ Deletion canceled.")
        }
    }

    override fun viewTaskDetails(taskId: UUID) {
        val task = getTaskUseCase(taskId)
        println("\n📋 Task Details:")
        println("Title: ${task.title}")
        println("Description: ${task.description}")
        println("Status: ${taskStateRepository.getStateById(taskId)}")
        println("Assignee: ${task.assigneeId}")
        println("Created At: ${task.creationDate}")
    }

    private fun updateField(fieldName: String, updatedTask: Task): Task {
        println("You selected: $fieldName")
        when (fieldName) {
            "Title" -> {
                val newTitle = inputReader.readString("Enter new title (leave blank to keep existing): ")
                if (newTitle.isNotBlank()) {
                    return updatedTask.copy(title = newTitle)
                } else {
                    println("No change to title.")
                    return updatedTask
                }
            }

            "Description" -> {
                val newDescription = inputReader.readString("Enter new description (leave blank to keep existing): ")
                if (newDescription.isNotBlank()) {
                    return updatedTask.copy(description = newDescription)
                } else {
                    println("No change to description.")
                    return updatedTask
                }
            }

            "Status" -> {
                val availableStatuses = taskStateRepository.getAllStates()
                availableStatuses.forEachIndexed { index, status ->
                    println("${index + 1}. ${status.name}")
                }

                val selectedIndex = inputReader.readInt(
                    prompt = "Enter the number of the new status (1 to ${availableStatuses.size}) or leave blank to keep existing: ",
                    min = 1,
                    max = availableStatuses.size
                )

                return if (selectedIndex in 1..availableStatuses.size) {
                    val newStatus = availableStatuses[selectedIndex - 1]
                    updatedTask.copy(stateId = newStatus.id)
                } else {
                    println("No change to status.")
                    return updatedTask
                }
            }

            else -> return updatedTask

        }
    }
}