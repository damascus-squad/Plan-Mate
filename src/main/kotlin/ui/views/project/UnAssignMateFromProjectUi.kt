package org.damascus.ui.views.project

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import logic.model.ActionType
import logic.model.History
import logic.model.Project
import logic.usecase.auditLog.SaveLogUseCase
import logic.usecase.project.UpdateProjectUseCase
import org.damascus.logic.usecase.project.UnassignMateUseCase
import ui.io.Display
import java.util.*

class UnAssignMateFromProjectUi (
    private val unassignMateUseCase: UnassignMateUseCase,
    private val display: Display,
    private val saveLogUseCase: SaveLogUseCase,
    private val updateProjectUseCase: UpdateProjectUseCase
){
    operator fun invoke (currentProject: Project, mateId: UUID) {

        if (currentProject.assignedMatesIds.contains(mateId).not()){
            display.writeError(errorMessage = " This Mate is Already UnAssigned")
            return
        }

        if (unassignMateUseCase(currentProject.id, mateId)) {
            display.write(prompt = "👤 Mate unassigned from project successfully!")
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
                assignedMatesIds = currentProject.assignedMatesIds.filter { it != mateId }.toMutableList()
            )

            updateProjectUseCase(projectId = updatedProject.id, updatedProject)

        } else {
            display.writeError(errorMessage = " Failed to UnAssign Mate.")
        }
    }
}