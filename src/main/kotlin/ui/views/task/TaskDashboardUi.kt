package org.damascus.ui.views.task

import logic.model.Task
import logic.model.User
import org.damascus.ui.views.auditLog.TaskLogUi
import ui.io.Display
import ui.util.UiAction

class TaskDashboardUi(
    private val updateTaskUi: UpdateTaskUi,
    private val deleteTaskUi: DeleteTaskUi,
    private val taskLogUi:TaskLogUi,
    private val display: Display,
) {
    operator fun invoke(currentUser: User, currentTask: Task){

        val taskActions = listOf(
            UiAction("update Task") { updateTaskUi(currentTask, currentUser ) },
            UiAction("Delete Task") { deleteTaskUi(currentTask, currentUser) },
            UiAction("Show Task History") { taskLogUi(currentTask.id) },
        )
        display.displayMenu(
            taskActions,
            menuTitle = "📁 Tasks Action:"
        )
    }
}