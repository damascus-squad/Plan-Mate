package org.damascus.ui.views.project

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import logic.model.ActionType
import logic.model.History
import logic.model.Project
import logic.model.User
import logic.usecase.auditLog.SaveLogUseCase
import logic.usecase.project.AssignMateUseCase
import logic.usecase.project.UpdateProjectUseCase
import ui.io.Display
import ui.util.printProjectDetails
import java.util.*

class AssignMateToProjectUi (
    private val assignMateUseCase: AssignMateUseCase,
    private val saveLogUseCase: SaveLogUseCase,
    private val updateProjectUseCase: UpdateProjectUseCase,
    private val display: Display
){
    operator fun invoke (currentProject: Project, mate: User, admin: User) {

        if (currentProject.assignedMatesIds.contains(mate.id)){
            display.writeError(errorMessage = " This Mate is Already Assigned Before.")
            return
        }

        if (assignMateUseCase(currentProject.id, mate.id)) {
            val updatedProject = currentProject.copy(
                assignedMatesIds = currentProject.assignedMatesIds.apply { add(mate.id) }
            )
            updateProjectUseCase(projectId = updatedProject.id, updatedProject)

            saveLogUseCase(
                History(
                    id = UUID.randomUUID(),
                    projectId = currentProject.id,
                    taskId = UUID.randomUUID(),
                    actionType = ActionType.PROJECT_ASSIGNED_USER,
                    userId = admin.id,
                    currentState = null,
                    newState = mate.username,
                    actionDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                )
            )

            display.write(prompt = "👥 Mate assigned to project successfully!")
            updatedProject.printProjectDetails()

        } else {
            display.writeError(errorMessage = " Failed to Assign Mate.")
        }
    }
}