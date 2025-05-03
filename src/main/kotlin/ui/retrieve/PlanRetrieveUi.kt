package org.damascus.ui.retrieve

import org.damascus.logic.usecase.ProjectUseCase.CreateProjectUseCase
import org.damascus.logic.usecase.ProjectUseCase.DeleteProjectUseCase
import org.damascus.logic.usecase.ProjectUseCase.GetAllProjectsUseCase
import org.damascus.logic.usecase.ProjectUseCase.UpdateProjectUseCase
import org.damascus.ui.io.ConsoleDisplay
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import logic.model.Project
import org.damascus.ui.io.ConsoleUserInput
import org.damascus.ui.util.TerminalColor
import org.damascus.ui.util.UiAction
import org.damascus.ui.views.project.ProjectViewCli
import java.util.*

class PlanRetrieveUi(
    private val consoleDisplay: ConsoleDisplay,
    private val consoleUserInput: ConsoleUserInput,
    private val projectViewCli: ProjectViewCli,
    private val createProjectUseCase: CreateProjectUseCase,
    private val getAllProjectsUseCase: GetAllProjectsUseCase,
    private val deleteProjectUseCase: DeleteProjectUseCase,
    private val updateProjectUseCase: UpdateProjectUseCase
) : PlanRetrieve {

    override fun displayProjects() {
        val projects = getAllProjectsUseCase()
        if (projects.isEmpty()) {
            projectViewCli.printMessageBox("No projects available.", TerminalColor.Red)
            return
        }

        projectViewCli.displayAllProjects(
            projects = projects,
            label = "Basic Info",
            contentSelector = { project ->
                mapOf(
                    "Name" to project.name,
                    "Mates Count" to project.assignedMatesIds.size,
                    "Created Date" to project.creationDate
                )
            }
        )
    }

    override fun createProject() {
        val name = consoleUserInput.readString("Enter project name:")
        val project = Project(
            id = UUID.randomUUID(),
            name = name,
            assignedMatesIds = mutableListOf(),
            creationDate = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        )

        if (createProjectUseCase(project)) {
            projectViewCli.printMessageBox("Added Project ${project.name}")
        } else {
            projectViewCli.printMessageBox("Project already exists.", TerminalColor.Red)
        }
    }

    override fun manageProject() {
        val projects = getAllProjectsUseCase()
        if (projects.isEmpty()) {
            projectViewCli.printMessageBox("No projects available.", TerminalColor.Red)
            return
        }

        projectViewCli.displayProjectsAsTable(projects)

        val choice = consoleUserInput.readInt("👉 Enter project number:", 1, projects.size)
        val selected = projects[choice - 1]

        val actions = listOf(
            UiAction("🗑️ Delete Project") {
                if (deleteProjectUseCase(selected.id)) {
                    projectViewCli.printMessageBox("Project deleted!")
                } else {
                    projectViewCli.printMessageBox("Failed to delete project.", TerminalColor.Red)
                }
            },
            UiAction("✏️ Update Project Name") {
                val newName = consoleUserInput.readString("Enter new project name:")
                val updated = selected.copy(name = newName)
                if (updateProjectUseCase(selected.id, updated)) {
                    projectViewCli.printMessageBox("Project updated successfully")
                } else {
                    projectViewCli.printMessageBox("Update failed.", TerminalColor.Red)
                }
            }
        )

        consoleDisplay.displayMenu(actions, "⚙️ Manage Project: '${selected.name}'")
    }
}
