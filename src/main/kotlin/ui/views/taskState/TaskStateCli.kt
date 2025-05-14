package org.damascus.ui.views.taskState

import logic.exception.DuplicateStateException
import logic.exception.StateNotFoundException
import logic.model.TaskState
import logic.repo.TaskStateRepository
import logic.usecase.state.CreateTaskStateUseCase
import logic.usecase.state.UpdateTaskStateUseCase
import ui.io.Display
import ui.io.InputReader
import ui.util.TerminalColor
import ui.util.UiAction
import ui.util.printTable
import ui.util.withStyle
import java.util.*

class TaskStateCLI(
    private val display: Display,
    private val inputReader: InputReader,
    private val taskStateRepository: TaskStateRepository,
    private val updateTaskStateUseCase: UpdateTaskStateUseCase,
    private val createTaskStateUseCase: CreateTaskStateUseCase
) : TaskStateController {

    fun start() {
        display.displayMenu(
            listOf(
                UiAction("Show All States") { showAllStates() },
                UiAction("Create New State") { createState() },
                UiAction("Delete State") { deleteState() },
                UiAction("Update State") { updateState() },
            ),
            menuTitle = "\n⚙️ Task State Management"
        )
    }

    override fun showAllStates() {
        val states = taskStateRepository.getAllStates()

        if (states.isEmpty()) {
            println("❗ No states found.".withStyle(TerminalColor.Red))
            return
        }

        val headers = listOf("State Name") + states.map { it.name }

        val usageRow = listOf("Used In") + states.map {
            val count = it.projectReferencesCount
            "$count project" + if (count != 1) "s" else ""
        }

        val rows = listOf(usageRow)

        printTable(headers, rows, TerminalColor.Green)
    }



    override fun createState() {
        val name = inputReader.readString("Enter name for new state: ")
        val taskState = TaskState(name = name, id = UUID.randomUUID(), projectReferencesCount = 1)

        try {
            createTaskStateUseCase(taskState)
            println("✅ Task state created successfully.")
        } catch (e: DuplicateStateException) {
            println("❌ Error: State with name \"$name\" already exists.")
        }

    }
    override fun deleteState() {
        val states = taskStateRepository.getAllStates()
        if (states.isEmpty()) {
            println("⚠️ No states to delete.")
            return
        }

        states.forEachIndexed { index, state ->
            println("${index + 1}. ${state.name} (References: ${state.projectReferencesCount})")
        }

        val index = inputReader.readInt("Select the state to delete (1-${states.size}): ", 1, states.size)
        val selectedState = states[index - 1]

        println("You selected: ${selectedState.name}")
        println("It is currently used in ${selectedState.projectReferencesCount} project(s).")

        val confirm = inputReader.readString("Are you sure you want to delete or reduce references? (yes/no): ")
        if (!confirm.equals("yes", ignoreCase = true)) {
            println("❌ Deletion canceled.")
            return
        }

        try {
            taskStateRepository.delete(selectedState)

            if (selectedState.projectReferencesCount > 1) {
                println("➖ Reference count decremented.")
            } else {
                println("🗑️ State deleted successfully.")
            }
        } catch (e: Exception) {
            println("❌ Error during deletion: ${e.message}")
        }
    }
    override fun updateState() {
        val states = taskStateRepository.getAllStates()
        if (states.isEmpty()) {
            println("⚠️ No states to update.")
            return
        }

        states.forEachIndexed { index, state ->
            println("${index + 1}. ${state.name}")
        }

        val index = inputReader.readInt("Select the state to update (1-${states.size}): ", 1, states.size)
        val selectedState = states[index - 1]

        val newName = inputReader.readString("Enter new name (leave blank to keep current): ")

        val updated = selectedState.copy(name = newName.ifBlank { selectedState.name })

        try {
            updateTaskStateUseCase(selectedState, updated)
            println("✅ State updated successfully.")
        } catch (e: DuplicateStateException) {
            println("❌ Error: A state with that name already exists.")
        } catch (e: StateNotFoundException) {
            println("❌ Error: The original state no longer exists.")
        }
    }
}
