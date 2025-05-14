package org.damascus.logic.usecase.auditLog

import org.damascus.logic.exception.NoLogException
import org.damascus.logic.model.History
import org.damascus.logic.repo.AuditLogsRepository
import java.util.*

class ManageAuditLogUseCase(
    private val auditLogsRepository: AuditLogsRepository
) {
    fun saveLog(history: History) = auditLogsRepository.saveLog(history)

    fun getTaskLogs(taskId: UUID): List<History> {
        return auditLogsRepository.getLogsByTaskId(taskId)
            .takeIf { it.isNotEmpty() }
            ?: throw NoLogException("No history found for the task: $taskId")
    }

    fun getProjectLogs(projectId: UUID): List<History> {
        return auditLogsRepository.getLogsByProjectId(projectId)
            .takeIf { it.isNotEmpty() }
            ?: throw NoLogException("No history found for the project: $projectId")
    }
}