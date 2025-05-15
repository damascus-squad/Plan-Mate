package org.damascus.ui.views.project

import org.damascus.logic.model.Project
import org.damascus.logic.model.User
import org.damascus.ui.io.Display
import org.damascus.ui.util.UiAction
import org.damascus.ui.util.printProjectDetails
import org.damascus.ui.views.auditLog.ProjectLogUi
import org.damascus.ui.views.task.TaskMainUi

class ProjectManagementUi(
    private val display: Display,
    private val updateProjectUi: UpdateProjectUi,
    private val deleteProjectUi: DeleteProjectUi,
    private val projectLogUi: ProjectLogUi,
    private val taskMainUi: TaskMainUi,
) {
    operator suspend fun invoke(currentProject: Project, currentUser: User) {
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
                refreshAction = { invoke(currentProject, currentUser) }
            ),

            UiAction(
                name = "🧩 Manage Project States",
                action = { /*projectStateManagementUi(currentProject)*/ }
            ),

            UiAction(
                name = "📋 View Tasks Board",
                action = { taskMainUi(currentProject, currentUser) },
                refreshAction = { invoke(currentProject, currentUser) }
            )
        )

        display.displayMenu(
            uiActionList = adminActions,
            menuTitle = "Project Management Dashboard"
        )
    }
}