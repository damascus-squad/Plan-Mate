package org.damascus.ui.views

import logic.exception.NoLogException
import logic.usecase.auditLog.GetLogsByProjectIdUseCase
import logic.usecase.auditLog.GetLogsByTaskIdUseCase
import org.damascus.ui.util.TerminalColor
import org.damascus.ui.util.withStyle
import java.util.*

class AuditLogView(
    private val getLogsByProjectId: GetLogsByProjectIdUseCase,
    private val getLogsByTaskId: GetLogsByTaskIdUseCase
) {
    fun viewProjectLog(projectId: UUID) {
        try {
            val log = getLogsByProjectId(projectId)
            println("📄 Log for Project ID [$projectId]:\n$log".withStyle(TerminalColor.Blue))
        } catch (e: NoLogException) {
            displayError("No log found for Project ID: $projectId")
        }
    }

    fun viewTaskLog(taskId: UUID) {
        try {
            val log = getLogsByTaskId(taskId)
            println("📄 Log for Task ID [$taskId]:\n$log".withStyle(TerminalColor.Blue))
        } catch (e: NoLogException) {
            displayError("No log found for Task ID: $taskId")
        }
    }

    private fun displayError(message: String?) {
        println("⚠️ $message".withStyle(TerminalColor.Red))
    }
}