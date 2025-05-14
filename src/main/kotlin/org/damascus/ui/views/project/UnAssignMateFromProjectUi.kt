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
import java.util.*

class UnAssignMateFromProjectUi(
    private val display: Display,
    private val manageProjectUseCase: ManageProjectUseCase,
    private val manageAuditLogUseCase: ManageAuditLogUseCase,
    private val manageMateAssignmentUseCase: ManageMateAssignmentUseCase
) {
    operator fun invoke(currentProject: Project, mate: User, admin: User) {

        if (currentProject.assignedMatesIds.contains(mate.id).not()) {
            display.writeError(errorMessage = " This Mate is Already UnAssigned")
            return
        }

        if (manageMateAssignmentUseCase.unAssign(currentProject.id, mate.id)) {
            display.write(prompt = "👤 Mate unassigned from project successfully!")
            manageAuditLogUseCase.saveLog(
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

            manageProjectUseCase.updateProject(projectId = updatedProject.id, updatedProject)
            updatedProject.printProjectDetails()

        } else {
            display.writeError(errorMessage = " Failed to UnAssign Mate.")
        }
    }
}