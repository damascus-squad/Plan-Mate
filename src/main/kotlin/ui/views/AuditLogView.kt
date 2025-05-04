package org.damascus.ui.views

import logic.usecase.util.NoLogsException
import org.damascus.logic.usecase.AuditLogUseCase
import org.damascus.ui.util.TerminalColor
import org.damascus.ui.util.withStyle
import java.util.UUID

class AuditLogView(
    private val auditUseCase: AuditLogUseCase,
) {
    fun viewProjectLog(projectId: UUID) {
        try {
            val log = auditUseCase.getLogsByProjectId(projectId)
            println("📄 Log for Project ID [$projectId]:\n$log".withStyle(TerminalColor.Blue))
        } catch (e: NoLogsException) {
            displayError("No log found for Project ID: $projectId")
        }
    }

    fun viewTaskLog(taskId: UUID) {
        try {
            val log = auditUseCase.getLogByTaskId(taskId)
            println("📄 Log for Task ID [$taskId]:\n$log".withStyle(TerminalColor.Blue))
        } catch (e: NoLogsException) {
            displayError("No log found for Task ID: $taskId")
        }
    }

    private fun displayError(message: String?) {
        println("⚠️ $message".withStyle(TerminalColor.Red))
    }
}