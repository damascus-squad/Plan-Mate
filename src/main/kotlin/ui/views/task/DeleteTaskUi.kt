package org.damascus.ui.views.task

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import logic.exception.TaskNotFoundException
import logic.model.*
import logic.usecase.auditLog.SaveLogUseCase
import logic.usecase.task.DeleteTaskUseCase
import ui.io.Display
import ui.io.InputReader
import java.util.*

class DeleteTaskUi(
    private val inputReader: InputReader,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val saveLogUseCase: SaveLogUseCase,
    private val display: Display
) {
    operator fun invoke(currentProject: Project, task: Task, admin: User) {
        val confirm = inputReader.readBoolean(prompt = "Are you sure you want to delete this task? (yes/no): ")
        if (confirm) {
            try {
                deleteTaskUseCase(task.id)
                saveLogUseCase(
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
            }catch (e: TaskNotFoundException) {
                display.writeError(errorMessage = "Failed to create task: ${e.message}")
            }

            display.write(prompt = "🗑️ Task deleted successfully!")
        } else {
            display.writeError(errorMessage = "Deletion canceled.")
        }
    }
}