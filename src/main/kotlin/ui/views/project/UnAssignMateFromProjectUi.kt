package org.damascus.ui.views.project

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import logic.model.ActionType
import logic.model.History
import logic.model.Project
import logic.model.User
import logic.usecase.auditLog.SaveLogUseCase
import logic.usecase.project.UpdateProjectUseCase
import org.damascus.logic.usecase.project.UnassignMateUseCase
import ui.io.Display
import ui.util.printProjectDetails
import java.util.*

class UnAssignMateFromProjectUi (
    private val unassignMateUseCase: UnassignMateUseCase,
    private val display: Display,
    private val saveLogUseCase: SaveLogUseCase,
    private val updateProjectUseCase: UpdateProjectUseCase
){
    operator fun invoke (currentProject: Project, mate: User, admin: User) {

        if (currentProject.assignedMatesIds.contains(mate.id).not()){
            display.writeError(errorMessage = " This Mate is Already UnAssigned")
            return
        }

        if (unassignMateUseCase(currentProject.id, mate.id)) {
            display.write(prompt = "👤 Mate unassigned from project successfully!")
            saveLogUseCase(
                History(
                    id = UUID.randomUUID(),
                    projectId = currentProject.id,
                    taskId = UUID.randomUUID(),
                    actionType = ActionType.PROJECT_UNASSIGNED_USER,
                    userId = admin.id,
                    currentState = null,
                    newState = mate.username,
                    actionDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                )
            )
            val updatedProject = currentProject.copy(
                assignedMatesIds = currentProject.assignedMatesIds.filter { it != mate.id }.toMutableList()
            )

            updateProjectUseCase(projectId = updatedProject.id, updatedProject)
            updatedProject.printProjectDetails()

        } else {
            display.writeError(errorMessage = " Failed to UnAssign Mate.")
        }
    }
}