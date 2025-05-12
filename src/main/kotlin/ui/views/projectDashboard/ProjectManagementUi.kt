package org.damascus.ui.views.projectDashboard

import logic.model.Project
import logic.model.User
import org.damascus.ui.views.auditLog.ProjectLogUi
import org.damascus.ui.views.project.DeleteProjectUi
import org.damascus.ui.views.project.UpdateProjectUi
import org.damascus.ui.views.task.CreateTaskUi
import org.damascus.ui.views.task.GetAllTasksByProjectIdUi
import ui.io.Display
import ui.util.UiAction
import ui.util.printProjectDetails
import ui.util.printProjectTable

class ProjectManagementUi(
    private val display: Display,
    private val updateProjectUi: UpdateProjectUi,
    private val deleteProjectUi: DeleteProjectUi,
    private val createTaskUi: CreateTaskUi,
    private val projectLogUi: ProjectLogUi,
    private val getAllTasksByProjectIdUi: GetAllTasksByProjectIdUi,
) {

    operator fun invoke(currentProject: Project, currentUser: User) {
        currentProject.printProjectDetails()
        val adminActions = listOf(
            UiAction("✏️ update Project") { updateProjectUi(currentUser, currentProject) },
            UiAction("🗑️ Delete Project") { deleteProjectUi(currentUser, currentProject) },
            UiAction("➕ Create Task") { createTaskUi(currentProject, currentUser) },
            UiAction("📜 Show History") { projectLogUi(currentProject.id) },
            UiAction("🧩 Manage Project States") { /*projectStateManagementUi(currentProject)*/ },
            UiAction("📋 View Tasks Board") { getAllTasksByProjectIdUi(currentProject) }
        )

        display.displayMenu(
            uiActionList = adminActions,
            menuTitle = "Project Management Dashboard"
        )
//        getAllTasksByProjectIdUi(currentProject)

    }

    private fun displayProjectDetails(currentProject: List<Project>) {
        currentProject.printProjectTable()
    }
}