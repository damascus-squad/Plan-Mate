package org.damascus.ui.views.task

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.damascus.logic.usecase.auditLog.ManageAuditLogUseCase
import org.damascus.logic.usecase.auth.ManageMateUseCase
import org.damascus.logic.usecase.state.ManageTaskStateUseCase
import org.damascus.logic.usecase.task.ManageTaskUseCase
import org.damascus.logic.model.ActionType
import org.damascus.logic.model.History
import org.damascus.logic.model.Project
import org.damascus.logic.model.Task
import org.damascus.logic.model.User
import org.damascus.ui.io.Display
import org.damascus.ui.io.InputReader
import org.damascus.ui.util.printTaskDetails
import java.util.*

class UpdateTaskStatusUi(
    private val inputReader: InputReader,
    private val display: Display,
    private val manageAuditLogUseCase: ManageAuditLogUseCase,
    private val manageMateUseCase: ManageMateUseCase,
    private val manageTaskUseCase: ManageTaskUseCase,
    private val manageTaskStateUseCase: ManageTaskStateUseCase
) {

    operator suspend fun invoke(currentProject: Project, currentUser: User, currentTask: Task): Task {
        val availableTaskStates = currentProject.allowedStatesIds.map { manageTaskStateUseCase.getTaskState(it) }

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
        manageTaskUseCase.updateTask(currentTask.id, updatedTask)

        manageAuditLogUseCase.saveLog(
            History(
                id = UUID.randomUUID(),
                projectId = currentProject.id,
                taskId = currentTask.id,
                actionType = ActionType.TASK_STATE_CHANGED,
                userId = currentUser.id,
                currentState = currentTask.stateId.let { manageTaskStateUseCase.getTaskState(it).name },
                newState = newStatus.name,
                actionDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            )
        )

        display.write(prompt = "✅ Status updated successfully to '${newStatus.name}'!")

        val assigneeUsername = currentTask.assigneeId?.let { manageMateUseCase.getMate(it).username } ?: "Unassigned"
        updatedTask.printTaskDetails(
            assignee = assigneeUsername,
            state = newStatus.name
        )

        return updatedTask
    }
}