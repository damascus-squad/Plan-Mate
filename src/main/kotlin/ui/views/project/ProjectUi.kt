package org.damascus.ui.views.project

import logic.model.User
import logic.usecase.project.GetAdminProjectsUseCase
import org.damascus.ui.views.projectDashboard.ProjectDashboardCli
import ui.io.Display
import ui.util.UiAction
import ui.views.project.SelectProjectUi

class ProjectUi(
    private val consoleDisplay: Display,
    private val getAdminProjectsUi: GetAdminProjectsUi,
    private val createProjectUi: CreateProjectUi,
    private val projectDashboardCli: ProjectDashboardCli,
    private val selectProjectUi: SelectProjectUi,
    private val getAdminProjectsUseCase: GetAdminProjectsUseCase
) {
    operator fun invoke(currentUser: User){
        getAdminProjectsUi()

        val dashboardActions = listOf(
            UiAction(name = "📜 Select Project") {
                projectDashboardCli.start(
                    selectProjectUi(getAdminProjectsUseCase()), currentUser)
            } ,
            UiAction(name = "➕ Create a New Project") { createProjectUi() }
//            UiAction(name = "✏️ Update an Existing Project") { updateProjectUi(currentUser) },
//            UiAction(name = "🗑️ Delete a Project") { deleteProjectUi() },
        )

        consoleDisplay.displayMenu(dashboardActions, menuTitle = "📁 Projects Menu:")
    }
}