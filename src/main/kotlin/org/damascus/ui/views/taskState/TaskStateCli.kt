package org.damascus.ui.views.taskState

import org.damascus.logic.exception.DuplicateStateException
import org.damascus.logic.exception.StateNotFoundException
import org.damascus.logic.repo.TaskStateRepository
import org.damascus.logic.usecase.state.ManageTaskStateUseCase
import org.damascus.ui.io.Display
import org.damascus.ui.io.InputReader
import org.damascus.ui.util.TerminalColor
import org.damascus.ui.util.UiAction
import org.damascus.ui.util.printTable
import org.damascus.ui.util.withStyle

class TaskStateCli(
    private val display: Display,
    private val inputReader: InputReader,
    private val taskStateRepository: TaskStateRepository,
    private val manageTaskState: ManageTaskStateUseCase
) {

    fun start() {
        display.displayMenu(
            listOf(
                UiAction("Show All States", { showAllStates() }),
                UiAction("Create New State", { createState() }),
                UiAction("Delete State", { deleteState() }),
                UiAction("Update State", { updateState() }),
            ),
            menuTitle = "\n⚙️ Task State Management"
        )
    }

    private fun showAllStates() {
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

    private fun createState() {
        val taskStateName = inputReader.readString("Enter name for new state: ")

        try {
            manageTaskState.createTaskState(taskStateName)
            println("✅ Task state created successfully.")
        } catch (e: DuplicateStateException) {
            println("❌ Error: State with name \"$taskStateName\" already exists.")
        }

    }

    private fun deleteState() {
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

    private fun updateState() {
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
            manageTaskState.updateTaskState(selectedState, updated)
            println("✅ State updated successfully.")
        } catch (e: DuplicateStateException) {
            println("❌ Error: A state with that name already exists.")
        } catch (e: StateNotFoundException) {
            println("❌ Error: The original state no longer exists.")
        }
    }
}
