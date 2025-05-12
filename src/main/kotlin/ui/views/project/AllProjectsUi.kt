package org.damascus.ui.views.project

import logic.model.User
import logic.usecase.project.GetAdminProjectsUseCase
import org.damascus.ui.views.projectDashboard.ProjectManagementUi
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
            getAdminProjectsUi()
            val dashboardActions = listOf(
                UiAction(name = "🔍 Select Project") {
                    val selectedProject = selectProjectUi(getAdminProjectsUseCase())
                    projectManagementUi(selectedProject, currentUser)
                    getAdminProjectsUi()
                },
                UiAction(name = "➕ Create a New Project") {
                    createProjectUi()
                },
            )

            consoleDisplay.displayMenu(
                uiActionList = dashboardActions,
                menuTitle = "📁 Projects Menu:")
    }
}