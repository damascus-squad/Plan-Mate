package org.damascus.ui.views.task

import org.damascus.logic.model.Project
import org.damascus.logic.model.User
import org.damascus.logic.model.UserRole
import org.damascus.logic.usecase.task.ManageTaskUseCase
import org.damascus.ui.io.Display
import org.damascus.ui.util.UiAction
import java.util.*

class UpdateTaskUi(
    private val display: Display,
    private val updateTaskAssigneeUi: UpdateTaskAssigneeUi,
    private val updateTaskTitleUi: UpdateTaskTitleUi,
    private val updateTaskDescriptionUi: UpdateTaskDescriptionUi,
    private val updateTaskStatusUi: UpdateTaskStatusUi,
    private val manageTaskUseCase: ManageTaskUseCase
) {
    operator fun invoke(taskId: UUID, user: User, project: Project) {
        var updatedTask = manageTaskUseCase.getTask(taskId)
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
