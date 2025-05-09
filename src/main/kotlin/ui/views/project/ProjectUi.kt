package org.damascus.ui.views.project

import logic.model.User
import ui.io.Display
import ui.util.UiAction

class ProjectUi(
    private val consoleDisplay: Display,
    private val getAdminProjectsUi: GetAdminProjectsUi,
    private val createProjectUi: CreateProjectUi,
    private val updateProjectUi: UpdateProjectUi,
    private val deleteProjectUi: DeleteProjectUi
) {
    operator fun invoke(currentUser: User){

        val dashboardActions = listOf(
            UiAction(name = "📜 Show All Projects") { getAdminProjectsUi() },
            UiAction(name = "➕ Create a New Project") { createProjectUi() },
            UiAction(name = "✏️ Update an Existing Project") { updateProjectUi(currentUser) },
            UiAction(name = "🗑️ Delete a Project") { deleteProjectUi() },
        )

        consoleDisplay.displayMenu(dashboardActions, menuTitle = "📁 Projects Menu:")
    }
}