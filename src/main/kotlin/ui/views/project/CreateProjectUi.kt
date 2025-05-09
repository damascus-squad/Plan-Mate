package org.damascus.ui.views.project

import logic.model.Project
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import logic.usecase.project.CreateProjectUseCase
import ui.io.Display
import ui.io.InputReader
import java.util.*

class CreateProjectUi (
    private val inputReader:InputReader,
    private val display: Display,
    private val createProjectUseCase: CreateProjectUseCase
){
    operator fun invoke(){
        val name = inputReader.readString("Enter project name:")
        val project = Project(
            id = UUID.randomUUID(),
            name = name,
            assignedMatesIds = mutableListOf(),
            allowedStatesIds = mutableListOf(),
            creationDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        )

        if (createProjectUseCase(project)) {
            display.write(prompt = "Added Project ${project.name}")
        } else {
            display.writeError(errorMessage = "Project already exists.")
        }
    }
}