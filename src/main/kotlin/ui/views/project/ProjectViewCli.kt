package org.damascus.ui.views.project

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import logic.model.Project
import org.damascus.logic.usecase.ProjectUseCase.CreateProjectUseCase
import org.damascus.logic.usecase.ProjectUseCase.GetAllProjectsUseCase
import org.damascus.ui.io.ConsoleUserInput
import org.damascus.ui.util.TerminalColor
import org.damascus.ui.util.printTable
import org.damascus.ui.util.withStyle
import java.util.*

class ProjectViewCli(
    private val consoleUserInput: ConsoleUserInput,
    private val createProjectUseCase: CreateProjectUseCase,
    private val getAllProjectsUseCase: GetAllProjectsUseCase
) : ProjectView {

    override fun displayProjects() {
        val projects = getAllProjectsUseCase()
        if (projects.isEmpty()) {
            printMessageBox("No projects available.", TerminalColor.Red)
            return
        }

        displayDetailsProjects(
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
            printMessageBox("Added Project ${project.name}")
        } else {
            printMessageBox("Project already exists.", TerminalColor.Red)
        }
    }

    override fun displayDetailsProjects(
        projects: List<Project>,
        label: String,
        contentSelector: ((Project) -> Map<String, Any?>)?
    ) {
        if (projects.isEmpty()) {
            println("No projects to display.")
            return
        }

        println("📋 Displaying projects by $label:")

        val headers = if (contentSelector != null) {
            listOf("ID") + contentSelector(projects.first()).keys.toList()
        } else {
            listOf("ID", "Name", "Assigned Mates", "Created Date")
        }

        val data = projects.map { project ->
            val contentMap = contentSelector?.invoke(project)
            if (contentMap != null) {
                listOf(project.id.toString().take(8)) + headers.drop(1).map { contentMap[it] ?: "-" }
            } else {
                listOf(
                    project.id.toString().take(8),
                    project.name,
                    project.assignedMatesIds.size.toString(),
                    project.creationDate.toString()
                )
            }
        }

        printTable(headers, data)
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
