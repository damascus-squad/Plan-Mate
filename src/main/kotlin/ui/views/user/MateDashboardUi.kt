package org.damascus.ui.views.user

import logic.model.User
import logic.usecase.project.GetMateProjectsUseCase
import org.damascus.ui.views.project.GetMateProjectsUi
import ui.io.Display
import ui.util.UiAction
import ui.views.project.SelectProjectUi

class MateDashboardUi(
    private val consoleDisplay: Display,
    private val getAllProjectsUi: GetMateProjectsUi,
    private val getAllProjectsUseCase: GetMateProjectsUseCase,
    private val getAllProjectsUseCase: GetMateProjectsUseCase,
    private val selectProjectUi: SelectProjectUi
) {
    operator fun invoke (currentUser: User) {
        val mateProjects = getAllProjectsUi(currentUser)
        val dashboardActions = listOf(
            UiAction("Create Task") { },
            UiAction("Select Project") { selectProjectUi(getAllProjectsUseCase(currentUser.id)) },
            UiAction("Show Project History") { },
        )

        consoleDisplay.displayMenu(dashboardActions, "Mate DASHBOARD")
    }
}
