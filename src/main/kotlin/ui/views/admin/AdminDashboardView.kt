package org.damascus.ui.views.admin

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import logic.exception.UnauthorizedActionException
import logic.model.Project
import logic.model.User
import logic.model.UserRole
import logic.usecase.auth.CreateMateUseCase
import logic.usecase.project.CreateProjectUseCase
import ui.io.Display
import ui.io.InputReader
import ui.util.TerminalColor
import ui.util.UiAction
import ui.util.withStyle
import ui.views.project.ProjectView
import ui.views.viewMateCreation
import java.util.*

class AdminDashboardView(
    private val consoleDisplay: Display,
    private val consoleUserInput: InputReader,
    private val projectView: ProjectView,
    private val createMateUseCase: CreateMateUseCase,
    private val createProjectUseCase: CreateProjectUseCase
) : AdminController {
    fun showDashboard(user: User) {
        if (user.userRole != UserRole.ADMIN) {
            println("Only admins can access this dashboard!".withStyle(TerminalColor.Red))
            return
        }

        val dashboardActions = listOf(
            UiAction("View Projects") { projectView.showAllProjects(user) },
            UiAction("Create New Mate") { viewMateCreation(user, createMateUseCase) },
            UiAction("Create New Project") { createProject(user) }
        )

        consoleDisplay.displayMenu(dashboardActions, "ADMIN DASHBOARD")
    }

    override fun createProject(currentUser: User) {
        if (currentUser.userRole != UserRole.ADMIN) {
            println("\n Only admins can create projects!\", TerminalColor.Red")
            throw UnauthorizedActionException("create project")
        }

        val name = consoleUserInput.readString("Enter project name:")
        val project = Project(
            id = UUID.randomUUID(),
            name = name,
            assignedMatesIds = mutableListOf(),
            allowedStatesIds = mutableListOf(),
            creationDate = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        )

        if (createProjectUseCase(project)) {
            println("Added Project ${project.name}")
        } else {
            println("Project already exists.".withStyle(TerminalColor.Red))
        }
    }
}