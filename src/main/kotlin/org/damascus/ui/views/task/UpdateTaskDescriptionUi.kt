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
import org.koin.core.annotation.Single
import java.util.*

@Single
class UpdateTaskDescriptionUi(
    private val inputReader: InputReader,
    private val display: Display,
    private val manageAuditLogUseCase: ManageAuditLogUseCase,
    private val manageMateUseCase: ManageMateUseCase,
    private val manageTaskUseCase: ManageTaskUseCase,
    private val manageTaskStateUseCase: ManageTaskStateUseCase
) {
    operator fun invoke(currentProject: Project, currentUser: User, currentTask: Task): Task {
        val newDescription = inputReader.readString(prompt = "Enter new description (or type 's' to keep current)")

        return if (newDescription.lowercase() != "s") {
            val updatedTask = currentTask.copy(description = newDescription)
            manageTaskUseCase.updateTask(currentTask.id, updatedTask)

            manageAuditLogUseCase.saveLog(
                History(
                    id = UUID.randomUUID(),
                    projectId = currentProject.id,
                    taskId = currentTask.id,
                    actionType = ActionType.TASK_DESCRIPTION_MODIFIED,
                    userId = currentUser.id,
                    currentState = currentTask.description,
                    newState = updatedTask.description,
                    actionDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                )
            )

            display.write(prompt = "✅ Description updated successfully!")

            val assigneeUsername =
                currentTask.assigneeId?.let { manageMateUseCase.getMate(it).username } ?: "Unassigned"
            updatedTask.printTaskDetails(
                assignee = assigneeUsername,
                state = manageTaskStateUseCase.getTaskState(updatedTask.stateId).name
            )
            updatedTask

        } else {
            display.write(prompt = "ℹ️ Description unchanged.")
            currentTask
        }
    }
}
