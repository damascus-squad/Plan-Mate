package ui.views.project

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import logic.exception.ProjectsNotAvailableException
import logic.exception.UnauthorizedActionException
import logic.model.Project
import logic.model.User
import logic.model.UserRole
import logic.usecase.project.CreateProjectUseCase
import logic.usecase.project.GetAllProjectsByMateIdUseCase
import logic.usecase.project.GetAllProjectsUseCase
import ui.io.ConsoleUserInput
import ui.util.TerminalColor
import ui.util.printTable
import ui.util.withStyle
import java.util.*

class ProjectViewCli(
    private val currentUser: User,
    private val consoleUserInput: ConsoleUserInput,
    private val createProjectUseCase: CreateProjectUseCase,
    private val getAllProjectsUseCase: GetAllProjectsUseCase,
    private val getAllProjectsByMateIdUseCase: GetAllProjectsByMateIdUseCase

) : ProjectView {

    override fun createProject() {
        if (currentUser.userRole != UserRole.ADMIN) {
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
        val projects = when (currentUser.userRole) {
            UserRole.ADMIN -> getAllProjectsUseCase()
            UserRole.MATE -> getAllProjectsByMateIdUseCase(currentUser.id)
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
