package org.damascus.ui.views.taskState

import org.damascus.logic.repo.TaskStateRepository
import org.damascus.ui.io.InputReader


class DeleteTaskStateUi(
    private val inputReader: InputReader,
    private val taskStateRepository: TaskStateRepository
) {
    operator fun invoke() {
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
}