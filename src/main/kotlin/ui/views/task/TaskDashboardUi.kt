package org.damascus.ui.views.task

import logic.model.Project
import logic.model.User
import logic.usecase.state.GetTaskStateByIdUseCase
import logic.usecase.task.GetTaskUseCase
import org.damascus.logic.usecase.auth.GetUserByIdUseCase
import org.damascus.ui.views.auditLog.TaskLogUi
import ui.io.Display
import ui.util.UiAction
import ui.util.printTaskDetails
import java.util.UUID

class TaskDashboardUi(
    private val updateTaskUi: UpdateTaskUi,
    private val deleteTaskUi: DeleteTaskUi,
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val getTaskStateByIdUseCase: GetTaskStateByIdUseCase,
    private val taskLogUi: TaskLogUi,
    private val display: Display,
    private val getTaskUseCase: GetTaskUseCase
) {
    operator fun invoke(currentUser: User, currentTaskId: UUID, currentProject: Project) {
        val updatedTask = getTaskUseCase(currentTaskId)

        val assigneeUsername = updatedTask.assigneeId?.let { getUserByIdUseCase(it).username } ?: "Unassigned"
        updatedTask.printTaskDetails(
            assignee = assigneeUsername,
            state = getTaskStateByIdUseCase(updatedTask.stateId).name
        )

        val taskActions = listOf(
            UiAction(
                name = "✏️ update Task",
                action = { updateTaskUi(updatedTask.id, currentUser, currentProject) },
                refreshAction = { invoke(currentUser, updatedTask.id, currentProject) }
            ),
            UiAction(
                name = "🗑️ Delete Task",
                action = { deleteTaskUi(currentProject, updatedTask, currentUser) },
                exitAfterAction = true
            ),
            UiAction(
                name = "📜 Show Task History",
                action = { taskLogUi(updatedTask.id) },
                refreshAction = { invoke(currentUser, updatedTask.id, currentProject) }
            )
        )
        display.displayMenu(
            taskActions,
            menuTitle = "📁 Tasks Action:"
        )
    }
}