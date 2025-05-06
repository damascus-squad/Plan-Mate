package org.damascus.ui.views

import logic.model.Admin
import org.damascus.logic.model.Role
import org.damascus.ui.io.InputReader
import org.damascus.ui.views.project.ProjectView
import org.damascus.ui.util.TerminalColor
import org.damascus.ui.util.withStyle
import logic.usecase.auth.CreateMateUseCase
import org.damascus.ui.io.ConsoleDisplay
import org.damascus.ui.io.ConsoleUserInput
import org.damascus.ui.util.UiAction


class AdminDashboardView(
    private val inputReader: InputReader,
    private val projectView: ProjectView,
    private val createMateUseCase: CreateMateUseCase
) {
    fun showDashboard(admin: Admin) {
        if (admin.role != Role.ADMIN) {
            println("Only admins can access this dashboard!".withStyle(TerminalColor.Red))
            return
        }

        val consoleDisplay = ConsoleDisplay(inputReader as ConsoleUserInput)

        val dashboardActions = listOf(
            UiAction("See ALL Projects") { projectView.showAllProjects() },
            UiAction("Create New Mate") { viewMateCreation(admin, createMateUseCase) }
        )

        consoleDisplay.displayMenu(dashboardActions, "ADMIN DASHBOARD")
    }
}


