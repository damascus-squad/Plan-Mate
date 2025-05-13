package org.damascus.ui.views.user

import logic.model.User
import logic.usecase.project.GetMateProjectsUseCase
import org.damascus.ui.views.project.GetMateProjectsUi
import org.damascus.ui.views.task.TaskMainUi
import ui.io.Display
import ui.util.UiAction
import ui.views.project.SelectProjectUi

class MateDashboardUi(
    private val consoleDisplay: Display,
    private val getAllProjectsUi: GetMateProjectsUi,
    private val getAllProjectsUseCase: GetMateProjectsUseCase,
    private val selectProjectUi: SelectProjectUi,
    private val taskDashboardUi: TaskMainUi
) {
    operator fun invoke (currentUser: User) {
        getAllProjectsUi(currentUser)
        val dashboardActions = listOf(
            UiAction("Select Project", { taskDashboardUi(
                currentProject = selectProjectUi(getAllProjectsUseCase(currentUser.id)),
                currentUser = currentUser
            ) }),
        )

        consoleDisplay.displayMenu(
            uiActionList = dashboardActions,
            menuTitle = "Mate DASHBOARD"
        )
    }
}
