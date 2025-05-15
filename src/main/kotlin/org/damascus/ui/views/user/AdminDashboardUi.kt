package org.damascus.ui.views.user

import org.damascus.logic.model.User
import org.damascus.ui.io.Display
import org.damascus.ui.util.UiAction
import org.damascus.ui.views.project.AllProjectsUi
import org.koin.core.annotation.Single

@Single
class AdminDashboardUi(
    private val consoleDisplay: Display,
    private val allProjectsUi: AllProjectsUi,
    private val mateManagementUi: MateManagementUi
) {
    operator fun invoke (admin: User) {
        val dashboardActions = listOf(
            UiAction("📁 Projects Management", { allProjectsUi(admin) }),
            UiAction("👥 User management", { mateManagementUi(admin) }),
        )
        consoleDisplay.displayMenu(
            uiActionList = dashboardActions,
            menuTitle = "ADMIN DASHBOARD"
        )
    }
}