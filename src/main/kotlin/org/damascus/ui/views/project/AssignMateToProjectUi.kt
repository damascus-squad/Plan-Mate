package org.damascus.ui.views.project

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.damascus.logic.model.ActionType
import org.damascus.logic.model.History
import org.damascus.logic.model.Project
import org.damascus.logic.model.User
import org.damascus.logic.usecase.auditLog.ManageAuditLogUseCase
import org.damascus.logic.usecase.project.ManageMateAssignmentUseCase
import org.damascus.logic.usecase.project.ManageProjectUseCase
import org.damascus.ui.io.Display
import org.damascus.ui.util.printProjectDetails
import org.koin.core.annotation.Single
import java.util.*

@Single
class AssignMateToProjectUi(
    private val manageMateAssignmentUseCase: ManageMateAssignmentUseCase,
    private val manageAuditLogUseCase: ManageAuditLogUseCase,
    private val manageProjectUseCase: ManageProjectUseCase,
    private val display: Display
) {
    operator suspend fun invoke(currentProject: Project, mate: User, admin: User) {

        if (currentProject.assignedMatesIds.contains(mate.id)) {
            display.writeError(errorMessage = " This Mate is Already Assigned Before.")
            return
        }

        if (manageMateAssignmentUseCase.assign(currentProject.id, mate.id)) {
            val updatedProject = currentProject.copy(
                assignedMatesIds = currentProject.assignedMatesIds.apply { add(mate.id) }
            )
            manageProjectUseCase.updateProject(projectId = updatedProject.id, updatedProject)

            manageAuditLogUseCase.saveLog(
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