package org.damascus.ui.views.auditLog

import org.damascus.logic.exception.NoLogException
import org.damascus.logic.model.ActionType
import org.damascus.logic.usecase.auditLog.ManageAuditLogUseCase
import org.damascus.logic.usecase.auth.ManageMateUseCase
import org.damascus.logic.usecase.project.ManageProjectUseCase
import org.damascus.ui.io.Display
import org.damascus.ui.util.formatDateTime
import java.util.*

class ProjectLogUi (
    private val display: Display,
    private val manageProjectUseCase: ManageProjectUseCase,
    private val manageAuditLogUseCase: ManageAuditLogUseCase,
    private val manageMateUseCase: ManageMateUseCase
){
    operator suspend fun invoke(projectId: UUID) {
        val projectName = manageProjectUseCase.getProject(projectId).name
        try {
            val logs = manageAuditLogUseCase.getProjectLogs(projectId)

            display.write(prompt = "📄 Log for Project Name [$projectName]:\n")

            logs.forEach { history ->
                val actionDateFormatted = formatDateTime(history.actionDate)
                val user = manageMateUseCase.getMate(history.userId)

                when (history.actionType) {
                    ActionType.PROJECT_CREATED -> {
                        display.write(prompt = "📝 Project created by user ${user.username} at $actionDateFormatted")
                    }
                    ActionType.PROJECT_TITLE_MODIFIED -> {
                        display.write(prompt = "📝 Project title changed from ${history.currentState} to ${history.newState} by user ${user.username} at $actionDateFormatted")
                    }
                    ActionType.PROJECT_ASSIGNED_USER->{
                        display.write("📝 Project Assigned New Mate ${history.newState} by user ${user.username} at $actionDateFormatted")
                    }
                    ActionType.PROJECT_UNASSIGNED_USER->{
                        display.write("📝 Project Unassigned Mate ${history.newState} by user ${user.username} at $actionDateFormatted")
                    }
                    ActionType.PROJECT_DELETED -> {
                        display.write("🗑️ Project deleted by user ${user.username} at $actionDateFormatted")
                    }
                    else -> {
                        display.write("❔ Unknown action: ${history.actionType} by user ${user.username} at $actionDateFormatted")
                    }
                }
            }
        } catch (e: NoLogException) {
            display.writeError("No log found for Project Name: $projectName")
        }
    }
}