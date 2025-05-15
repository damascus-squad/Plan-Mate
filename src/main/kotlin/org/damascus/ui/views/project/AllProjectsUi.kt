package org.damascus.ui.views.project

import org.damascus.logic.model.User
import org.damascus.logic.usecase.project.ManageProjectUseCase
import org.damascus.ui.io.Display
import org.damascus.ui.util.UiAction

class AllProjectsUi(
    private val consoleDisplay: Display,
    private val getAdminProjectsUi: GetAdminProjectsUi,
    private val createProjectUi: CreateProjectUi,
    private val projectManagementUi: ProjectManagementUi,
    private val selectProjectUi: SelectProjectUi,
    private val manageProjectUseCase: ManageProjectUseCase
) {
    operator suspend fun invoke(currentUser: User) {
        val hasProjects = getAdminProjectsUi()

        val dashboardActions =
            if (hasProjects) {
                listOf(
                    UiAction(
                        name = "🔍 Select Project",
                        action = {
                            val selectedProject = selectProjectUi(manageProjectUseCase.getAllProjects())
                            projectManagementUi(selectedProject, currentUser)
                        },
                        refreshAction = { invoke(currentUser) }
                    ),
                    createProjectUiAction(createProjectUi, currentUser)
                )
            } else listOf(createProjectUiAction(createProjectUi, currentUser))

        consoleDisplay.displayMenu(
            uiActionList = dashboardActions,
            menuTitle = "📁 Projects Menu:"
        )
    }

    private suspend fun createProjectUiAction(createProjectUi: CreateProjectUi, currentUser: User): UiAction {
        return UiAction(
            name = "➕ Create a New Project",
            action = { createProjectUi(currentUser) },
            refreshAction = { invoke(currentUser) }
        )
    }
}