package org.damascus.ui.views.task

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import logic.model.*
import logic.usecase.auditLog.SaveLogUseCase
import logic.usecase.state.GetTaskStateByIdUseCase
import logic.usecase.task.UpdateTaskUseCase
import org.damascus.logic.usecase.auth.GetUserByIdUseCase
import ui.io.Display
import ui.io.InputReader
import ui.util.printTaskDetails
import java.util.*

class UpdateTaskStatusUi(
    private val inputReader: InputReader,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val saveLogUseCase: SaveLogUseCase,
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val display: Display,
    private val getTaskStateByIdUseCase: GetTaskStateByIdUseCase
) {

    operator fun invoke(currentProject: Project, currentUser: User, currentTask: Task): Task {
        val availableTaskStates = currentProject.allowedStatesIds.map { getTaskStateByIdUseCase(it) }

        display.write(prompt = "Available task states:")

        availableTaskStates.forEachIndexed { index, state ->
            display.write(prompt = "${index + 1}. ${state.name}")
        }

        val selectedIndex = inputReader.readInt(
            prompt = "Enter the number of the new status (1 to ${availableTaskStates.size}) or 0 to keep existing",
            min = 1,
            max = availableTaskStates.size
        )

        val newStatus = availableTaskStates[selectedIndex - 1]
        val updatedTask = currentTask.copy(stateId = newStatus.id)
        updateTaskUseCase(currentTask.id, updatedTask)

        saveLogUseCase(
            History(
                id = UUID.randomUUID(),
                projectId = currentProject.id,
                taskId = currentTask.id,
                actionType = ActionType.TASK_STATE_CHANGED,
                userId = currentUser.id,
                currentState = currentTask.stateId.let { getTaskStateByIdUseCase(it).name },
                newState = newStatus.name,
                actionDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            )
        )

        display.write(prompt = "✅ Status updated successfully to '${newStatus.name}'!")

        val assigneeUsername = currentTask.assigneeId?.let { getUserByIdUseCase(it).username } ?: "Unassigned"
        updatedTask.printTaskDetails(
            assignee = assigneeUsername,
            state = newStatus.name
        )

        return updatedTask
    }
}