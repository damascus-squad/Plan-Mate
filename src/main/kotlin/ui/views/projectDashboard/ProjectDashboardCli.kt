package org.damascus.ui.views.projectDashboard

import logic.model.Project
import logic.model.TaskState
import logic.model.User
import logic.model.UserRole
import logic.usecase.task.GetTasksByProjectUseCase
import org.damascus.ui.views.auditLog.ProjectLogUi
import org.damascus.ui.views.project.DeleteProjectUi
import org.damascus.ui.views.project.UpdateProjectUi
import org.damascus.ui.views.task.CreateTaskUi
import org.damascus.ui.views.task.GetAllTasksByProjectIdUi
import ui.io.Display
import ui.util.UiAction
import ui.util.printTable
import java.util.*

class ProjectDashboardCli(
    private val display: Display,
    private val updateProjectUi: UpdateProjectUi,
    private val deleteProjectUi: DeleteProjectUi,
    private val createTaskUi: CreateTaskUi,
    private val projectLogUi: ProjectLogUi,
    private val getAllTasksByProjectIdUi: GetAllTasksByProjectIdUi,
)  {

    private val dummyStates = listOf(
        TaskState(UUID.fromString("11111111-1111-1111-1111-111111111111"),"TODO",1),
        TaskState(UUID.fromString("22222222-2222-2222-2222-222222222222"), "In Progress", 1),
        TaskState(UUID.fromString("33333333-3333-3333-3333-333333333333"), "Done", 1),
    )

     fun start(currentProject: Project, currentUser: User) {

        val adminActions = listOf(
            UiAction("update Project") { updateProjectUi(currentUser, currentProject ) },
            UiAction("Delete Project") { deleteProjectUi(currentUser,currentProject) },
            UiAction("Create Task") { createTaskUi(currentProject, currentUser) },
            UiAction("Show History") { projectLogUi(currentProject.id) },
            UiAction("Display Tasks Board") { getAllTasksByProjectIdUi( currentProject) }
        )

        val mateActions = listOf(
            UiAction("Create Task") { createTaskUi(currentProject, currentUser) },
            UiAction("Show History") { projectLogUi(currentProject.id) },
        )

        val actions = when (currentUser.userRole) {
            UserRole.ADMIN -> adminActions
            UserRole.MATE -> mateActions
        }

        display.displayMenu(actions, menuTitle = "Project Dashboard")
        getAllTasksByProjectIdUi(currentProject)
    }
}