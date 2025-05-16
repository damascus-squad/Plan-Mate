package org.damascus.ui.views.taskState

import org.damascus.logic.exception.DuplicateStateException
import org.damascus.logic.exception.StateNotFoundException
import org.damascus.logic.repo.TaskStateRepository
import org.damascus.logic.usecase.state.ManageTaskStateUseCase
import org.damascus.ui.io.InputReader


class UpdateTaskStateUi(
    private val inputReader: InputReader,
    private val taskStateRepository: TaskStateRepository,
    private val manageTaskState: ManageTaskStateUseCase
) {
    operator fun invoke() {
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