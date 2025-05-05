package org.damascus.ui.views.project

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import logic.exception.ProjectsNotAvailableException
import logic.exception.UnauthorizedActionException
import logic.model.Project
import logic.model.User
import org.damascus.logic.model.Role
import org.damascus.logic.usecase.ProjectUseCase.CreateProjectUseCase
import org.damascus.logic.usecase.ProjectUseCase.GetAllProjectsByMateIdUseCase
import org.damascus.logic.usecase.ProjectUseCase.GetAllProjectsUseCase
import org.damascus.ui.io.ConsoleUserInput
import org.damascus.ui.util.TerminalColor
import org.damascus.ui.util.printTable
import org.damascus.ui.util.withStyle
import java.util.*

class ProjectViewCli(
    private val currentUser: User,
    private val consoleUserInput: ConsoleUserInput,
    private val createProjectUseCase: CreateProjectUseCase,
    private val getAllProjectsUseCase: GetAllProjectsUseCase,
    private val getAllProjectsByMateIdUseCase: GetAllProjectsByMateIdUseCase

) : ProjectView {

    override fun createProject() {
        if (currentUser.role != Role.ADMIN) {
            printMessageBox("Only admins can create projects!", TerminalColor.Red)
            throw UnauthorizedActionException("create project")
        }

        val name = consoleUserInput.readString("Enter project name:")
        val project = Project(
            id = UUID.randomUUID(),
            name = name,
            assignedMatesIds = mutableListOf(),
            creationDate = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        )

        if (createProjectUseCase(project)) {
            printMessageBox("Added Project ${project.name}")
        } else {
            printMessageBox("Project already exists.", TerminalColor.Red)
        }
    }

    override fun showAllProjects(): Project? {
        val projects = when (currentUser.role) {
            Role.ADMIN -> getAllProjectsUseCase()
            Role.MATE -> getAllProjectsByMateIdUseCase(currentUser.id)
        }

        if (projects.isEmpty()) {
            printMessageBox("No projects available.", TerminalColor.Red)
            throw ProjectsNotAvailableException("No projects available to display.")
        }


        val headers = listOf("No", "Name", "ID", "Mates Count", "Created Date")
        val rows = projects.mapIndexed { index, project ->
            listOf(
                (index + 1).toString(),
                project.name,
                project.id.toString().take(8),
                project.assignedMatesIds.size.toString(),
                project.creationDate.toString()
            )
        }

        printTable(headers, rows)

        val choice = consoleUserInput.readInt(
            prompt = "\nEnter project number to select: ",
            min = 1,
            max = projects.size
        )

        val selectedProject = projects[choice - 1]
        println("\nYou selected: ${selectedProject.name} (ID: ${selectedProject.id})")
        return selectedProject
    }


    fun printMessageBox(
        message: String,
        color: TerminalColor = TerminalColor.Green
    ) {
        val border = "=".repeat(message.length + 2).withStyle(color)
        println("╔$border╗")
        println("║ ${message.withStyle(color)} ║")
        println("╚$border╝")
    }
}
