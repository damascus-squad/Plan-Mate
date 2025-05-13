package org.damascus.ui.views.project

import logic.model.Project
import logic.model.User
import org.damascus.ui.views.auditLog.ProjectLogUi
import org.damascus.ui.views.task.TaskUI
import ui.io.Display
import ui.util.UiAction
import ui.util.printProjectDetails

class ProjectManagementUi(
    private val display: Display,
    private val updateProjectUi: UpdateProjectUi,
    private val deleteProjectUi: DeleteProjectUi,
    private val projectLogUi: ProjectLogUi,
    private val taskUI: TaskUI,
) {
    operator fun invoke(currentProject: Project, currentUser: User) {
        currentProject.printProjectDetails()

        val adminActions = listOf(
            UiAction(
                name = "✏️ update Project",
                action = { updateProjectUi(currentUser, currentProject) },
                refreshAction = { invoke(currentProject, currentUser) }
            ),

            UiAction(
                name = "🗑️ Delete Project",
                action = { deleteProjectUi(currentUser, currentProject) },
                exitAfterAction = true
            ),

            UiAction(
                name = "📜 Show History",
                action = { projectLogUi(currentProject.id) },
                refreshAction = {invoke(currentProject, currentUser)}
            ),

            UiAction(
                name = "🧩 Manage Project States",
                action = { /*projectStateManagementUi(currentProject)*/ }
            ),

            UiAction(
                name = "📋 View Tasks Board",
                action = { taskUI(currentProject, currentUser) },
                refreshAction = { invoke(currentProject, currentUser) }
            )
        )

        display.displayMenu(
            uiActionList = adminActions,
            menuTitle = "Project Management Dashboard"
        )
    }
}