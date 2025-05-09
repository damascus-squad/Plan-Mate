package org.damascus.ui.views.project

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import logic.model.ActionType
import logic.model.History
import logic.model.Project
import logic.usecase.auditLog.SaveLogUseCase
import logic.usecase.project.AssignMateUseCase
import logic.usecase.project.UpdateProjectUseCase
import ui.io.Display
import java.util.*

class AssignMateToProjectUi (
    private val assignMateUseCase: AssignMateUseCase,
    private val saveLogUseCase: SaveLogUseCase,
    private val updateProjectUseCase: UpdateProjectUseCase,
    private val display: Display
){
    operator fun invoke (currentProject: Project, mateId: UUID) {

        if (currentProject.assignedMatesIds.contains(mateId)){
            display.writeError(errorMessage = " This Mate is Already Assigned Before.")
            return
        }

        if (assignMateUseCase(currentProject.id, mateId)) {
            display.write(prompt = "👥 Mate assigned to project successfully!")

            saveLogUseCase(
                History(
                    id = UUID.randomUUID(),
                    projectId = currentProject.id,
                    taskId = UUID.randomUUID(),
                    actionType = ActionType.PROJECT_MODIFIED,
                    userId = mateId,
                    currentStateId = History.NO_UUID,
                    newStateId = History.NO_UUID,
                    actionDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                )
            )
            val updatedProject = currentProject.copy(
                assignedMatesIds = currentProject.assignedMatesIds.toMutableList().apply { add(mateId) }
            )
            updateProjectUseCase(projectId = updatedProject.id, updatedProject)

        } else {
            display.writeError(errorMessage = " Failed to Assign Mate.")
        }
    }
}