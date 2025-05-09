package org.damascus.ui.views.admin

import logic.model.User
import ui.io.Display
import ui.util.UiAction

class MateDashboardUi(
    private val consoleDisplay: Display
) {
    operator fun invoke (currentUser: User) {

        val dashboardActions = listOf(
            UiAction("Create Task") { },
            UiAction("Show Projects") { },
            UiAction("Show Tasks History") { },
            UiAction("Show Project History") { },
        )

        consoleDisplay.displayMenu(dashboardActions, "Mate DASHBOARD")
    }
}
