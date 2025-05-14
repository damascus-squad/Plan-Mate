package org.damascus.ui.views.task

import org.damascus.logic.model.Project
import org.damascus.logic.model.User
import org.damascus.logic.usecase.auth.ManageMateUseCase
import org.damascus.logic.usecase.state.ManageTaskStateUseCase
import org.damascus.logic.usecase.task.ManageTaskUseCase
import org.damascus.ui.io.Display
import org.damascus.ui.util.UiAction
import org.damascus.ui.util.printTaskDetails
import org.damascus.ui.views.auditLog.TaskLogUi
import java.util.*

class TaskDashboardUi(
    private val display: Display,
    private val updateTaskUi: UpdateTaskUi,
    private val deleteTaskUi: DeleteTaskUi,
    private val taskLogUi: TaskLogUi,
    private val manageMateUseCase: ManageMateUseCase,
    private val manageTaskUseCase: ManageTaskUseCase,
    private val manageTaskStateUseCase: ManageTaskStateUseCase
) {
    operator fun invoke(currentUser: User, currentTaskId: UUID, currentProject: Project) {
        val updatedTask = manageTaskUseCase.getTask(currentTaskId)

        val assigneeUsername = updatedTask.assigneeId?.let { manageMateUseCase.getMate(it).username } ?: "Unassigned"
        updatedTask.printTaskDetails(
            assignee = assigneeUsername,
            state = manageTaskStateUseCase.getTaskState(updatedTask.stateId).name
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