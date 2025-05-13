package org.damascus.ui.views.task

import logic.model.Project
import logic.model.User
import logic.usecase.task.GetTasksByProjectUseCase
import ui.io.Display
import ui.util.UiAction

class TaskMainUi(
    private val getAllTasksByProjectIdUi: GetAllTasksByProjectIdUi,
    private val selectTaskUi: SelectTaskUi,
    private val getTasksByProjectUseCase: GetTasksByProjectUseCase,
    private val display: Display,
    private val createTaskUi: CreateTaskUi,
    private val taskDashboardUi: TaskDashboardUi
) {
    operator fun invoke(currentProject: Project, currentUser: User) {
        val hasTasks = getAllTasksByProjectIdUi(currentProject)

        val dashboardActions = if (hasTasks) {
            listOf(
                UiAction(
                    name = "📜 Task Management",
                    action = {
                        val selectedTask = selectTaskUi(getTasksByProjectUseCase(currentProject.id))
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