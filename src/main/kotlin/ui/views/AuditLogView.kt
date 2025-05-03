package org.damascus.ui.views

import logic.usecase.util.NoLogsException
import org.damascus.logic.usecase.AuditLogUseCase
import org.damascus.ui.exception.InputException
import org.damascus.ui.io.InputReader
import org.damascus.ui.util.TerminalColor
import org.damascus.ui.util.withStyle
import java.util.UUID

class AuditLogView(
    private val auditUseCase: AuditLogUseCase,
    private val inputReader: InputReader,
) {
    private val quitCommands = setOf("quit", "exit", "q")

    fun runAuditLogView(projectOrTask: String) {
        projectOrTask.trim().lowercase()

        if (projectOrTask in quitCommands) return

        when (projectOrTask) {
            "project" -> handleProjectLog()
            "task" -> handleTaskLog()
            else -> displayError("$projectOrTask is invalid choice. Please type 'project' or 'task'.")
        }
    }

    private fun handleProjectLog() {
        val input = inputReader.readString("Enter Project ID (or 'quit' to cancel):")
        if (input.trim().lowercase() in quitCommands) return

        try {
            val projectId = parseUUID(input)
            val log = auditUseCase.getLogsByProjectId(projectId)
            println("📄 Log for Project ID [$projectId]:\n$log".withStyle(TerminalColor.Blue))
        } catch (e: InputException) {
            displayError("Invalid UUID format.")
        } catch (e: NoLogsException) {
            displayError("No log found for Project ID: $input")
        }
    }

    private fun handleTaskLog() {
        val input = inputReader.readString("Enter Task ID (or 'quit' to cancel):")
        if (input.trim().lowercase() in quitCommands) return

        try {
            val taskId = parseUUID(input)
            val log = auditUseCase.getLogByTaskId(taskId)
            println("📄 Log for Task ID [$taskId]:\n$log".withStyle(TerminalColor.Blue))
        } catch (e: InputException) {
            displayError("Invalid UUID format.")
        } catch (e: NoLogsException) {
            displayError("No log found for Task ID: $input")
        }
    }

    private fun parseUUID(input: String): UUID {
        return try {
            UUID.fromString(input)
        } catch (e: IllegalArgumentException) {
            throw InputException("Invalid UUID format.")
        }
    }

    private fun displayError(message: String?) {
        println("⚠️ $message".withStyle(TerminalColor.Red))
    }
}
