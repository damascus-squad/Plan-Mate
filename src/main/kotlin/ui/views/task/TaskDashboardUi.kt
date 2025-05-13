package org.damascus.ui.views.task

import logic.model.Project
import logic.model.Task
import logic.model.User
import logic.usecase.state.GetTaskStateByIdUseCase
import org.damascus.logic.usecase.auth.GetUserByIdUseCase
import org.damascus.ui.views.auditLog.TaskLogUi
import ui.io.Display
import ui.util.UiAction
import ui.util.printTaskDetails

class TaskDashboardUi(
    private val updateTaskUi: UpdateTaskUi,
    private val deleteTaskUi: DeleteTaskUi,
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val getTaskStateByIdUseCase: GetTaskStateByIdUseCase,
    private val taskLogUi: TaskLogUi,
    private val display: Display,
) {
    operator fun invoke(currentUser: User, currentTask: Task, currentProject: Project) {

        val assigneeUsername = currentTask.assigneeId?.let { getUserByIdUseCase(it).username } ?: "Unassigned"
        currentTask.printTaskDetails(
            assignee = assigneeUsername,
            state = getTaskStateByIdUseCase(currentTask.id).name
        )

        val taskActions = listOf(
            UiAction(
                name = "✏️ update Task",
                action = { updateTaskUi(currentTask, currentUser, currentProject) }
            ),
            UiAction(
                name = "🗑️ Delete Task",
                action = { deleteTaskUi(currentProject, currentTask, currentUser) },
                exitAfterAction = true
            ),
            UiAction(
                name = "📜 Show Task History",
                action = { taskLogUi(currentTask.id) }
            )
        )
        display.displayMenu(
            taskActions,
            menuTitle = "📁 Tasks Action:"
        )
    }
}