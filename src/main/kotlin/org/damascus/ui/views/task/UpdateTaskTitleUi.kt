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

class UpdateTaskTitleUi(
    private val inputReader: InputReader,
    private val display: Display,
    private val manageAuditLogUseCase: ManageAuditLogUseCase,
    private val manageMateUseCase: ManageMateUseCase,
    private val manageTaskUseCase: ManageTaskUseCase,
    private val manageTaskStateUseCase: ManageTaskStateUseCase
) {
    operator suspend fun invoke(currentProject: Project, currentUser: User, currentTask: Task): Task {
        val newTitle = inputReader.readString(prompt = "Enter new title (or type 's' to keep current)")

        return if (newTitle.lowercase() != "s") {
            val updatedTask = currentTask.copy(title = newTitle)
            manageTaskUseCase.updateTask(updatedTask.id, updatedTask)

            manageAuditLogUseCase.saveLog(
                History(
                    id = UUID.randomUUID(),
                    projectId = currentProject.id,
                    taskId = currentTask.id,
                    actionType = ActionType.TASK_TITLE_MODIFIED,
                    userId = currentUser.id,
                    currentState = currentTask.title,
                    newState = updatedTask.title,
                    actionDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                )
            )

            display.write(prompt = "✅ Title updated successfully!")

            val assigneeUsername =
                currentTask.assigneeId?.let { manageMateUseCase.getMate(it).username } ?: "Unassigned"
            updatedTask.printTaskDetails(
                assignee = assigneeUsername,
                state = manageTaskStateUseCase.getTaskState(updatedTask.stateId).name
            )
            updatedTask

        } else {
            display.write(prompt = "ℹ️ Title unchanged.")
            currentTask
        }
    }
}