package org.damascus.ui.views.task

import logic.model.Project
import logic.model.Task
import logic.model.User
import logic.model.UserRole
import ui.io.Display
import ui.util.UiAction

class UpdateTaskUi(
    private val display: Display,
    private val updateTaskAssigneeUi: UpdateTaskAssigneeUi,
    private val updateTaskTitleUi: UpdateTaskTitleUi,
    private val updateTaskDescriptionUi: UpdateTaskDescriptionUi,
    private val updateTaskStatusUi: UpdateTaskStatusUi
) {
    operator fun invoke(task: Task, user: User, project: Project) {
        val actions = mutableListOf(
            UiAction(
                name = "Title",
                action = { updateTaskTitleUi(project, user, task) }
            ),
            UiAction(
                name = "Description",
                action = { updateTaskDescriptionUi(project, user, task) }
            ),
            UiAction(
                name = "Status",
                action = { updateTaskStatusUi(project, user, task) }
            )
        )

        if (user.userRole == UserRole.ADMIN) {
            actions.add(
                UiAction(
                    name = "Change Assignee",
                    action = { updateTaskAssigneeUi(user, task, project) }
                )
            )
        }

        display.displayMenu(actions, menuTitle = "\nSelect the field you want to update:")
    }

}
