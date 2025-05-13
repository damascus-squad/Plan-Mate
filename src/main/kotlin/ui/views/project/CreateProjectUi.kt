package org.damascus.ui.views.project

import logic.model.Project
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import logic.model.ActionType
import logic.model.History
import logic.model.User
import logic.usecase.auditLog.SaveLogUseCase
import logic.usecase.project.CreateProjectUseCase
import ui.io.Display
import ui.io.InputReader
import java.util.*

class CreateProjectUi(
    private val inputReader: InputReader,
    private val display: Display,
    private val createProjectUseCase: CreateProjectUseCase,
    private val saveLogUseCase: SaveLogUseCase
) {
    operator fun invoke(admin: User) {
        val name = inputReader.readString(prompt = "Enter project name")
        val project = Project(
            id = UUID.randomUUID(),
            name = name,
            assignedMatesIds = mutableListOf(),
            allowedStatesIds = mutableListOf(),
            creationDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        )

        if (createProjectUseCase(project)) {
            display.write(prompt = "Project ${project.name} Added Successfully")

            saveLogUseCase(
                History(
                    id = UUID.randomUUID(),
                    projectId = project.id,
                    taskId = History.NO_UUID,
                    actionType = ActionType.PROJECT_CREATED,
                    userId = admin.id,
                    currentState = null,
                    newState = null,
                    actionDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                )
            )
        } else {
            display.writeError(errorMessage = "Project already exists.")
        }
    }
}