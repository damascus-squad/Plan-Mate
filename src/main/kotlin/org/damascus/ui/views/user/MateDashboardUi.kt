package org.damascus.ui.views.user

import org.damascus.logic.model.User
import org.damascus.logic.usecase.project.ManageProjectUseCase
import org.damascus.ui.io.Display
import org.damascus.ui.util.UiAction
import org.damascus.ui.views.project.GetMateProjectsUi
import org.damascus.ui.views.project.SelectProjectUi
import org.damascus.ui.views.task.TaskMainUi
import org.koin.core.annotation.Single

@Single
class MateDashboardUi(
    private val consoleDisplay: Display,
    private val getAllProjectsUi: GetMateProjectsUi,
    private val manageProjectUseCase: ManageProjectUseCase,
    private val selectProjectUi: SelectProjectUi,
    private val taskDashboardUi: TaskMainUi
) {
    operator suspend fun invoke(currentUser: User) {
        getAllProjectsUi(currentUser)
        val dashboardActions = listOf(
            UiAction("Select Project", {
                taskDashboardUi(
                    currentProject = selectProjectUi(manageProjectUseCase.getMateProjects(currentUser.id)),
                    currentUser = currentUser
                )
            }),
        )

        consoleDisplay.displayMenu(
            uiActionList = dashboardActions,
            menuTitle = "Mate DASHBOARD"
        )
    }
}
