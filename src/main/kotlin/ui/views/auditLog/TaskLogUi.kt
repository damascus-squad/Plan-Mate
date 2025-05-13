package org.damascus.ui.views.auditLog

import logic.exception.NoLogException
import logic.model.ActionType
import logic.usecase.auditLog.GetLogsByTaskIdUseCase
import logic.usecase.task.GetTaskUseCase
import org.damascus.logic.usecase.auth.GetUserByIdUseCase
import ui.io.Display
import ui.util.formatDateTime
import java.util.*

class TaskLogUi(
    private val display: Display,
    private val getLogsByTaskId: GetLogsByTaskIdUseCase,
    private val getTaskUseCase: GetTaskUseCase,
    private val getUserByIdUseCase: GetUserByIdUseCase
) {
    operator fun invoke(taskId: UUID) {
        val taskName = getTaskUseCase(taskId).title
        try {
            val logs = getLogsByTaskId(taskId)
            logs.forEach { history ->
                val actionDateFormatted = formatDateTime(history.actionDate)
                val user = getUserByIdUseCase(history.userId)

                when (history.actionType) {
                    ActionType.TASK_CREATED -> {
                        display.write(prompt = "📝 Task created by user ${user.username} at $actionDateFormatted")
                    }

                    ActionType.TASK_TITLE_MODIFIED -> {
                        display.write(prompt = "📝 Task title changed from ${history.currentState} to ${history.newState} by user ${user.username} at $actionDateFormatted")
                    }

                    ActionType.TASK_ASSIGNED_USER_MODIFIED -> {
                        display.write(prompt = "📝 Task Assigned User Modified from ${history.currentState} to ${history.newState} by user ${user.username} at $actionDateFormatted")
                    }

                    ActionType.TASK_DELETED -> {
                        display.write(prompt = "🗑️ Task deleted by user ${user.username} at $actionDateFormatted")
                    }

                    ActionType.TASK_DESCRIPTION_MODIFIED -> {
                        display.write(prompt = "📝 Task Description changed from ${history.currentState} to ${history.newState} by user ${user.username} at $actionDateFormatted")
                    }

                    ActionType.TASK_STATE_CHANGED -> {
                        display.write(prompt = "📝 Task State changed from ${history.currentState} to ${history.newState} by user ${user.username} at $actionDateFormatted")
                    }

                    else -> {
                        display.write("❔ Unknown action: ${history.actionType} by user ${user.username} at $actionDateFormatted")
                    }
                }
            }
        } catch (e: NoLogException) {
            display.writeError(errorMessage = "No log found for Task ID: $taskName")
        }
    }
}