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

class UpdateTaskDescriptionUi(
    private val inputReader: InputReader,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val saveLogUseCase: SaveLogUseCase,
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val display: Display,
    private val getTaskStateByIdUseCase: GetTaskStateByIdUseCase
) {
    operator fun invoke(currentProject: Project, currentUser: User, currentTask: Task): Task {
        val newDescription = inputReader.readString(prompt = "Enter new description (or type 's' to keep current)")

        return if (newDescription.lowercase() != "s") {
            val updatedTask = currentTask.copy(description = newDescription)
            updateTaskUseCase(currentTask.id, updatedTask)

            saveLogUseCase(
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

            val assigneeUsername = currentTask.assigneeId?.let { getUserByIdUseCase(it).username } ?: "Unassigned"
            updatedTask.printTaskDetails(
                assignee = assigneeUsername,
                state = getTaskStateByIdUseCase(updatedTask.stateId).name
            )
            updatedTask

        } else {
            display.write(prompt = "ℹ️ Description unchanged.")
            currentTask
        }
    }
}
