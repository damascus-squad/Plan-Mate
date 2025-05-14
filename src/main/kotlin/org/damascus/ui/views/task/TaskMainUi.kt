package org.damascus.ui.views.task

import org.damascus.logic.model.Project
import org.damascus.logic.model.User
import org.damascus.logic.usecase.task.ManageTaskUseCase
import org.damascus.ui.io.Display
import org.damascus.ui.util.UiAction

class TaskMainUi(
    private val display: Display,
    private val selectTaskUi: SelectTaskUi,
    private val createTaskUi: CreateTaskUi,
    private val taskDashboardUi: TaskDashboardUi,
    private val getAllTasksByProjectIdUi: GetAllTasksByProjectIdUi,
    private val manageTaskUseCase: ManageTaskUseCase
) {
    operator fun invoke(currentProject: Project, currentUser: User) {
        val hasTasks = getAllTasksByProjectIdUi(currentProject)

        val dashboardActions = if (hasTasks) {
            listOf(
                UiAction(
                    name = "📜 Task Management",
                    action = {
                        val selectedTask = selectTaskUi(manageTaskUseCase.getProjectTasks(currentProject.id))
                        taskDashboardUi(currentUser, selectedTask.id, currentProject)
                    },
                    refreshAction = { invoke(currentProject, currentUser) }
                ),
                createTaskUiAction(currentProject, currentUser) { invoke(currentProject, currentUser) }
            )
        } else listOf(createTaskUiAction(currentProject, currentUser) { invoke(currentProject, currentUser) })


        display.displayMenu(
            uiActionList = dashboardActions,
            menuTitle = "📁 Tasks Menu:"
        )
    }

    private fun createTaskUiAction(currentProject: Project, currentUser: User, refreshAction: () -> Unit): UiAction {
        return UiAction(
            name = "➕ Create a New Task",
            action = { createTaskUi(currentProject, currentUser) },
            refreshAction = { refreshAction() }
        )
    }
}