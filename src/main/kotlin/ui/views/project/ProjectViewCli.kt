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

    override fun showAllProjects() {
        val projects = getAllProjectsUseCase()
        if (projects.isEmpty()) {
            printMessageBox("No projects available.", TerminalColor.Red)
            return
        }

        val headers = listOf("ID", "Name", "Mates Count", "Created Date")
        val rows = projects.map {
            listOf(
                it.id.toString().take(8),
                it.name,
                it.assignedMatesIds.size.toString(),
                it.creationDate.toString()
            )
        }

        printTable(headers, rows)
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
