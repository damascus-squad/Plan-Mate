package org.damascus.ui.views.task

import logic.model.Task
import logic.model.User
import logic.usecase.project.GetProjectUseCase
import logic.usecase.state.GetTaskStateByIdUseCase
import logic.usecase.task.GetTaskUseCase
import logic.usecase.task.UpdateTaskUseCase
import ui.io.Display
import ui.io.InputReader
import ui.util.UiAction

class UpdateTaskUi(
    private val getTaskUseCase: GetTaskUseCase,
    private val display: Display,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val inputReader: InputReader,
    private val getProjectUseCase: GetProjectUseCase,
    private val getTaskStateByIdUseCase: GetTaskStateByIdUseCase
) {
    operator fun invoke(task: Task, user: User) {
        var updatedTask = getTaskUseCase(task.id)

        display.displayMenu(
            listOf(
                UiAction("Title") { updatedTask = updateField("Title", updatedTask) },
                UiAction("Description") { updatedTask = updateField("Description", updatedTask) },
                UiAction("Status") { updatedTask = updateField("Status", updatedTask) }
            ),
            menuTitle = "\nSelect the field you want to update:"
        )

        updateTaskUseCase(task.id, updatedTask)

        println("✅ Task updated successfully!")
    }

    private fun updateField(fieldName: String, updatedTask: Task): Task {
        println("You selected: $fieldName")
        return when (fieldName) {
            "Title" -> {
                val newTitle = inputReader.readString("Enter new title (leave blank to keep existing): ")
                if (newTitle.isNotBlank()) updatedTask.copy(title = newTitle) else {
                    println("No change to title.")
                    updatedTask
                }
            }

            "Description" -> {
                val newDescription = inputReader.readString("Enter new description (leave blank to keep existing): ")
                if (newDescription.isNotBlank()) updatedTask.copy(description = newDescription) else {
                    println("No change to description.")
                    updatedTask
                }
            }

            "Status" -> {
                val project = getProjectUseCase(updatedTask.projectId)
                val availableTaskStates = project.allowedStatesIds.map { getTaskStateByIdUseCase(it) }

                availableTaskStates.forEachIndexed { index, state ->
                    println("${index + 1}. ${state.name}")
                }

                val selectedIndex = inputReader.readInt(
                    prompt = "Enter the number of the new status (1 to ${availableTaskStates.size}) or 0 to keep existing: ",
                    min = 0,
                    max = availableTaskStates.size
                )

                if (selectedIndex in 1..availableTaskStates.size) {
                    val newStatus = availableTaskStates[selectedIndex - 1]
                    updatedTask.copy(stateId = newStatus.id)
                } else {
                    println("No change to status.")
                    updatedTask
                }
            }

            else -> updatedTask // fallback for safety
        }
    }
}
