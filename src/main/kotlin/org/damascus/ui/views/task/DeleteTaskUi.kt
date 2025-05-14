package org.damascus.ui.views.task

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.damascus.logic.exception.TaskNotFoundException
import org.damascus.logic.usecase.auditLog.ManageAuditLogUseCase
import org.damascus.logic.usecase.task.ManageTaskUseCase
import org.damascus.logic.model.ActionType
import org.damascus.logic.model.History
import org.damascus.logic.model.Project
import org.damascus.logic.model.Task
import org.damascus.logic.model.User
import org.damascus.ui.io.Display
import org.damascus.ui.io.InputReader
import java.util.*

class DeleteTaskUi(
    private val inputReader: InputReader,
    private val display: Display,
    private val manageAuditLogUseCase: ManageAuditLogUseCase,
    private val manageTaskUseCase: ManageTaskUseCase
) {
    operator fun invoke(currentProject: Project, task: Task, admin: User) {
        val confirm = inputReader.readBoolean(prompt = "Are you sure you want to delete this task? (yes/no): ")
        if (confirm) {
            try {
                manageTaskUseCase.deleteTask(task.id)
                manageAuditLogUseCase.saveLog(
                    History(
                        id = UUID.randomUUID(),
                        projectId = currentProject.id,
                        taskId = task.id,
                        actionType = ActionType.TASK_CREATED,
                        userId = admin.id,
                        currentState = null,
                        newState = null,
                        actionDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                    )
                )
            } catch (e: TaskNotFoundException) {
                display.writeError(errorMessage = "Failed to create task: ${e.message}")
            }

            display.write(prompt = "🗑️ Task deleted successfully!")
        } else {
            display.writeError(errorMessage = "Deletion canceled.")
        }
    }
}