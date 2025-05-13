package org.damascus.ui.views.project

import logic.model.User
import logic.usecase.project.GetAdminProjectsUseCase
import ui.io.Display
import ui.util.UiAction
import ui.views.project.SelectProjectUi

class AllProjectsUi(
    private val consoleDisplay: Display,
    private val getAdminProjectsUi: GetAdminProjectsUi,
    private val createProjectUi: CreateProjectUi,
    private val projectManagementUi: ProjectManagementUi,
    private val selectProjectUi: SelectProjectUi,
    private val getAdminProjectsUseCase: GetAdminProjectsUseCase
) {
    operator fun invoke(currentUser: User) {
        val hasProjects = getAdminProjectsUi()

        val dashboardActions =
            if (hasProjects) {
                listOf(
                    UiAction(
                        name = "🔍 Select Project",
                        action = {
                            val selectedProject = selectProjectUi(getAdminProjectsUseCase())
                            projectManagementUi(selectedProject, currentUser)
                        },
                        refreshAction = { invoke(currentUser) }
                    ),
                    createProjectUiAction(createProjectUi, currentUser)
                )
            } else listOf(createProjectUiAction(createProjectUi, currentUser))

        consoleDisplay.displayMenu(
            uiActionList = dashboardActions,
            menuTitle = "📁 Projects Menu:"
        )
    }

    private fun createProjectUiAction(createProjectUi: CreateProjectUi, currentUser: User): UiAction {
        return UiAction(
            name = "➕ Create a New Project",
            action = { createProjectUi(currentUser) },
            refreshAction = { invoke(currentUser) }
        )
    }
}