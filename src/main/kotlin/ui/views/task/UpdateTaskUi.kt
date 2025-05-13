package org.damascus.ui.views.task

import logic.model.Project
import logic.model.User
import logic.model.UserRole
import logic.usecase.task.GetTaskUseCase
import ui.io.Display
import ui.util.UiAction
import java.util.UUID

class UpdateTaskUi(
    private val display: Display,
    private val updateTaskAssigneeUi: UpdateTaskAssigneeUi,
    private val updateTaskTitleUi: UpdateTaskTitleUi,
    private val updateTaskDescriptionUi: UpdateTaskDescriptionUi,
    private val updateTaskStatusUi: UpdateTaskStatusUi,
    private val getTaskUseCase: GetTaskUseCase
) {
    operator fun invoke(taskId: UUID, user: User, project: Project) {
        var updatedTask = getTaskUseCase(taskId)
        val actions = mutableListOf(
            UiAction(
                name = "Title",
                action = { updatedTask = updateTaskTitleUi(project, user, updatedTask) }
            ),
            UiAction(
                name = "Description",
                action = { updatedTask = updateTaskDescriptionUi(project, user, updatedTask) }
            ),
            UiAction(
                name = "Status",
                action = { updatedTask = updateTaskStatusUi(project, user, updatedTask) }
            )
        )

        if (user.userRole == UserRole.ADMIN) {
            actions.add(
                UiAction(
                    name = "Change Assignee",
                    action = { updatedTask = updateTaskAssigneeUi(user, updatedTask, project) }
                )
            )
        }

        display.displayMenu(actions, menuTitle = "\nSelect the field you want to update:")

    }

}
