package ui.views.project

import logic.exception.ProjectsNotAvailableException
import logic.model.Project
import logic.model.User
import logic.model.UserRole
import logic.usecase.project.GetAllProjectsByMateIdUseCase
import logic.usecase.project.GetAllProjectsUseCase
import org.damascus.ui.views.projectDashboard.ProjectDashboardCli
import org.damascus.ui.views.projectDashboard.ProjectDashboardController
import ui.io.InputReader
import ui.util.TerminalColor
import ui.util.printTable
import ui.util.withStyle

class ProjectViewCli(
    private val consoleUserInput: InputReader,
    private val getAllProjectsUseCase: GetAllProjectsUseCase,
    private val getAllProjectsByMateIdUseCase: GetAllProjectsByMateIdUseCase,
    private val projectDashboardCli: ProjectDashboardController

) : ProjectView {

    override fun showAllProjects(currentUser: User): Project {
        val projects = when (currentUser.userRole) {
            UserRole.ADMIN -> getAllProjectsUseCase()
            UserRole.MATE -> getAllProjectsByMateIdUseCase(currentUser.id)
        }

        if (projects.isEmpty()) {
            println("No projects available.".withStyle(TerminalColor.Red))
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
        projectDashboardCli.start(selectedProject.id, currentUser)
        return selectedProject
    }
}
