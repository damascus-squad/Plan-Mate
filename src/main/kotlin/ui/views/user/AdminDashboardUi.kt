package org.damascus.ui.views.admin

import logic.model.User
import org.damascus.ui.views.project.CreateProjectUi
import org.damascus.ui.views.project.ProjectUi
import ui.io.Display
import ui.util.UiAction

class AdminDashboardUi(
    private val consoleDisplay: Display,
    private val projectUi: ProjectUi,
    private val createProjectUi: CreateProjectUi
) {
    operator fun invoke (currentUser: User) {

        val dashboardActions = listOf(
            UiAction("📁 Projects Management" ) { projectUi(currentUser) },
            UiAction("👥 User management") { createProjectUi() },
            UiAction("🧩 👥 State Management") { createProjectUi() }
        )
        consoleDisplay.displayMenu(dashboardActions, "ADMIN DASHBOARD")
    }
}