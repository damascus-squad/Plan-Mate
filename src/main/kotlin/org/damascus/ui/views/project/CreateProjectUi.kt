package org.damascus.ui.views.project

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.damascus.logic.model.ActionType
import org.damascus.logic.model.History
import org.damascus.logic.model.Project
import org.damascus.logic.model.User
import org.damascus.logic.usecase.auditLog.ManageAuditLogUseCase
import org.damascus.logic.usecase.project.ManageProjectUseCase
import org.damascus.ui.io.Display
import org.damascus.ui.io.InputReader
import java.util.*

class CreateProjectUi(
    private val inputReader: InputReader,
    private val display: Display,
    private val manageProjectUseCase: ManageProjectUseCase,
    private val manageAuditLogUseCase: ManageAuditLogUseCase
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

        if (manageProjectUseCase.createProject(project)) {
            display.write(prompt = "Project ${project.name} Added Successfully")

            manageAuditLogUseCase.saveLog(
                History(
                    id = UUID.randomUUID(),
                    projectId = project.id,
                    taskId = History.Companion.NO_UUID,
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