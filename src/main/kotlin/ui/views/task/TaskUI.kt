package org.damascus.ui.views.task

import logic.model.Project
import logic.model.User
import logic.usecase.task.GetTasksByProjectUseCase
import ui.io.Display
import ui.util.UiAction

class TaskUI(
    private val getAllTasksByProjectIdUi: GetAllTasksByProjectIdUi,
    private val selectTaskUi: SelectTaskUi,
    private val getTasksByProjectUseCase: GetTasksByProjectUseCase,
    private val display: Display,
    private val createTaskUi: CreateTaskUi
) {
    operator fun invoke(currentProject: Project, currentUser: User) {

        getAllTasksByProjectIdUi(currentProject)

        val dashboardActions = listOf(
            UiAction(name = "📜 Select Task") { selectTaskUi(getTasksByProjectUseCase(currentProject.id)) },
            UiAction(name = "➕ Create a New Task") { createTaskUi(currentProject,currentUser) }
        )

        display.displayMenu(dashboardActions, menuTitle = "📁 Tasks Menu:")
    }
}