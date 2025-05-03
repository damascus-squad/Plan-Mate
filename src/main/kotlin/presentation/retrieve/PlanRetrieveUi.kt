package org.damascus.presentation.retrieve

import kotlinx.datetime.toLocalDateTime
import logic.model.Project
import org.damascus.logic.usecase.project.CreateProjectUseCase
import org.damascus.logic.usecase.project.DeleteProjectUseCase
import org.damascus.logic.usecase.project.GetAllProjectsUseCase
import org.damascus.logic.usecase.project.UpdateProjectUseCase
import org.damascus.presentation.TerminalColor
import org.damascus.presentation.UiAction
import org.damascus.presentation.io.ConsoleDisplay
import org.damascus.presentation.io.ConsoleUserInput
import org.damascus.presentation.withStyle
import java.util.*

class PlanRetrieveUi(
    private val consoleDisplay: ConsoleDisplay,
    private val consoleUserInput: ConsoleUserInput,
    private val createProjectUseCase: CreateProjectUseCase,
    private val getAllProjectsUseCase: GetAllProjectsUseCase,
    private val deleteProjectUseCase: DeleteProjectUseCase,
    private val updateProjectUseCase: UpdateProjectUseCase
) : PlanRetrieve {

    override fun displayProjects() {
        val projects = getAllProjectsUseCase()
        consoleDisplay.displayAllProjects(
            projects = projects,
            label = "basic info",
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
            creationDate = kotlinx.datetime.Clock.System.now()
                .toLocalDateTime(kotlinx.datetime.TimeZone.UTC)
        )
        if (createProjectUseCase(project)) {
            println("✅ Project '${project.name}' created with ID: ${project.id}")
        } else {
            println("⚠️ Project already exists.")
        }
    }

    override fun manageProject() {
        val projects = getAllProjectsUseCase()
        if (projects.isEmpty()) {
            println("❌ No projects available.")
            return
        }

        println("📋 Select a project to manage:")
        consoleDisplay.displayProjectsAsTable(projects)


        val choice = consoleUserInput.readInt("Enter project number:", 1, projects.size)
        val selected = projects[choice - 1]

        showProjectActions(selected)
    }

    private fun showProjectActions(project: Project) {
        val actions = listOf(
            UiAction("🗑️ Delete Project") {
                if (deleteProjectUseCase(project.id)) {
                    printMessageBox("Project deleted!")
                } else {
                    printMessageBox("Failed to delete project.", TerminalColor.Red)
                }
            },
            UiAction("✏️ Update Project Name") {
                val newName = consoleUserInput.readString("Enter new project name:")
                val updated = project.copy(name = newName)
                if (updateProjectUseCase(project.id, updated)) {
                    printMessageBox("Project updated successfully")
                } else {
                    printMessageBox("Update failed.", TerminalColor.Red)
                }
            }
        )

        println("\n⚙️ Manage Project: '${project.name}'")
        actions.forEachIndexed { i, action -> println("${i + 1}. ${action.name}") }
        val input = consoleUserInput.readInt("Select an action:", 1, actions.size)
        actions[input - 1].action()
    }

    private fun printMessageBox(message: String, color: TerminalColor = TerminalColor.Green) {
        val border = "=".repeat(message.length + 2).withStyle(color)
        println("╔$border╗")
        println("║ ${message.withStyle(color)} ║")
        println("╚$border╝")
    }


}
