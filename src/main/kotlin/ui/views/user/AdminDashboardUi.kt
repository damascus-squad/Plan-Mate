package org.damascus.ui.views.admin

import logic.model.User
import org.damascus.ui.views.project.CreateProjectUi
import org.damascus.ui.views.project.AllProjectsUi
import ui.io.Display
import ui.util.UiAction
import org.damascus.ui.views.user.CreateMateUi
import org.damascus.ui.views.user.MateManagementUi

class AdminDashboardUi(
    private val consoleDisplay: Display,
    private val allProjectsUi: AllProjectsUi,
    private val mateManagementUi: MateManagementUi
) {
    operator fun invoke (admin: User) {
        val dashboardActions = listOf(
            UiAction("📁 Projects Management" ) { allProjectsUi(admin) },
            UiAction("👥 User management") { mateManagementUi(admin) },
        )
        consoleDisplay.displayMenu(
            uiActionList = dashboardActions,
            menuTitle = "ADMIN DASHBOARD"
        )
    }
}