package org.damascus.ui.views.auditLog

import logic.exception.NoLogException
import logic.model.ActionType
import logic.usecase.auditLog.GetLogsByProjectIdUseCase
import logic.usecase.project.GetProjectUseCase
import org.damascus.logic.usecase.auth.GetUserByIdUseCase
import ui.io.Display
import ui.util.formatDateTime
import java.util.*

class ProjectLogUi (
    private val display: Display,
    private val getProjectUseCase: GetProjectUseCase,
    private val getLogsByProjectId: GetLogsByProjectIdUseCase,
    private val getUserByIdUseCase: GetUserByIdUseCase
){
    operator fun invoke(projectId: UUID) {
        val projectName = getProjectUseCase(projectId).name
        try {
            val logs = getLogsByProjectId(projectId)

            display.write(prompt = "📄 Log for Project Name [$projectName]:\n")

            logs.forEach { history ->
                val actionDateFormatted = formatDateTime( history.actionDate)
                val user = getUserByIdUseCase(history.userId)

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