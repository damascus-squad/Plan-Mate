package ui.views

import logic.model.User
import logic.model.UserRole
import logic.usecase.auth.CreateMateUseCase
import ui.io.ConsoleDisplay
import ui.util.TerminalColor
import ui.util.UiAction
import ui.util.withStyle
import ui.views.project.ProjectView

class AdminDashboardView(
    private val consoleDisplay: ConsoleDisplay,
    private val projectView: ProjectView,
    private val createMateUseCase: CreateMateUseCase
) {
    fun showDashboard(user: User) {
        if (user.userRole != UserRole.ADMIN) {
            println("Only admins can access this dashboard!".withStyle(TerminalColor.Red))
            return
        }

        val dashboardActions = listOf(
            UiAction("See ALL Projects") { projectView.showAllProjects() },
            UiAction("Create New Mate") { viewMateCreation(user, createMateUseCase) }
        )

        consoleDisplay.displayMenu(dashboardActions, "ADMIN DASHBOARD")
    }
}