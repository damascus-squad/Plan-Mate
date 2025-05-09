package org.damascus.ui.views.auditLog

import logic.exception.NoLogException
import logic.usecase.auditLog.GetLogsByTaskIdUseCase
import logic.usecase.task.GetTaskUseCase
import ui.io.Display
import java.util.*

class TaskLogUi (
    private val display: Display,
    private val getLogsByTaskId: GetLogsByTaskIdUseCase,
    private val getTaskUseCase: GetTaskUseCase
){
    operator fun invoke(taskId: UUID) {
        val taskName = getTaskUseCase(taskId).title
        try {
            val log = getLogsByTaskId(taskId)
            println("📄 Log for Task [$taskName]:\n$log")
        } catch (e: NoLogException) {
            display.writeError(errorMessage = "No log found for Task ID: $taskName")
        }
    }
}