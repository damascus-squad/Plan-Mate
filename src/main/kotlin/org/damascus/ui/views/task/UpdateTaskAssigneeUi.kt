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
import org.damascus.ui.util.printTaskDetails
import org.damascus.ui.views.user.SelectMateUi
import java.util.*

class UpdateTaskAssigneeUi(
    private val display: Display,
    private val selectMateUi: SelectMateUi,
    private val manageAuditLogUseCase: ManageAuditLogUseCase,
    private val manageMateUseCase: ManageMateUseCase,
    private val manageTaskUseCase: ManageTaskUseCase,
    private val manageTaskStateUseCase: ManageTaskStateUseCase
) {
    operator suspend fun invoke(admin: User, currentTask: Task, currentProject: Project): Task {
        val mates = currentProject.assignedMatesIds.map { mateId ->
            manageMateUseCase.getMate(mateId)
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
        manageTaskUseCase.updateTask(currentTask.id, updatedTask)

        manageAuditLogUseCase.saveLog(
            History(
                id = UUID.randomUUID(),
                projectId = currentProject.id,
                taskId = currentTask.id,
                actionType = ActionType.TASK_ASSIGNED_USER_MODIFIED,
                userId = admin.id,
                currentState = currentTask.assigneeId?.let { manageMateUseCase.getMate(it).username } ?: "Unassigned",
                newState = selectedMate.username,
                actionDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            )
        )

        updatedTask.printTaskDetails(
            assignee = selectedMate.username,
            state = manageTaskStateUseCase.getTaskState(updatedTask.stateId).name
        )

        display.write("✅ Task assigned successfully to user '${selectedMate.username}'.")
        return updatedTask
    }
}
