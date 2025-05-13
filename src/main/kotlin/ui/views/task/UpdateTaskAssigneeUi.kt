package org.damascus.ui.views.task

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import logic.model.*
import logic.usecase.auditLog.SaveLogUseCase
import logic.usecase.state.GetTaskStateByIdUseCase
import logic.usecase.task.UpdateTaskUseCase
import org.damascus.logic.usecase.auth.GetUserByIdUseCase
import org.damascus.ui.views.user.SelectMateUi
import ui.io.Display
import ui.util.printTaskDetails
import java.util.*

class UpdateTaskAssigneeUi(
    private val display: Display,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val saveLogUseCase: SaveLogUseCase,
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val selectMateUi: SelectMateUi,
    private val getTaskStateByIdUseCase: GetTaskStateByIdUseCase
) {
    operator fun invoke(admin: User, currentTask: Task, currentProject: Project): Task {
        val mates = currentProject.assignedMatesIds.map { mateId ->
            getUserByIdUseCase(mateId)
        }

        if (mates.isEmpty()) {
            display.writeError(errorMessage = "No mates are allowed on this project.")
            return currentTask
        }

        val selectedMate = selectMateUi(mates)

        if (selectedMate == null) {
            display.writeError(errorMessage = "Invalid selection.")
            return currentTask
        }

        if (currentTask.assigneeId == selectedMate.id) {
            display.write(prompt = "ℹ️ Task is already assigned to ${selectedMate.username}. No changes made.")
            return currentTask
        }

        val updatedTask = currentTask.copy(assigneeId = selectedMate.id)
        updateTaskUseCase(currentTask.id, updatedTask)

        saveLogUseCase(
            History(
                id = UUID.randomUUID(),
                projectId = currentProject.id,
                taskId = currentTask.id,
                actionType = ActionType.TASK_ASSIGNED_USER_MODIFIED,
                userId = admin.id,
                currentState = currentTask.assigneeId?.let { getUserByIdUseCase(it).username } ?: "Unassigned",
                newState = selectedMate.username,
                actionDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            )
        )

        updatedTask.printTaskDetails(
            assignee = selectedMate.username,
            state = getTaskStateByIdUseCase(updatedTask.stateId).name
        )

        display.write("✅ Task assigned successfully to user '${selectedMate.username}'.")
        return updatedTask
    }
}
