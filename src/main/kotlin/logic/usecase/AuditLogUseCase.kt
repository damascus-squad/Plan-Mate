package org.damascus.logic.usecase

import logic.exception.NoLogException
import logic.repo.AuditLogRepository
import org.damascus.logic.model.History
import java.util.*

class AuditLogUseCase(
    private val auditLogRepository: AuditLogRepository
) {
    fun saveLog(history: History) {
        auditLogRepository.saveLog(history)
    }

    fun getLogByProjectId(projectId: UUID): List<History> {
        return auditLogRepository.getLogByProjectId(projectId)
            .takeIf { it.isNotEmpty() }
            ?: throw NoLogException("No history found for the project: $projectId")
    }

    fun getLogByTaskId(taskId: UUID): List<History> {
        return auditLogRepository.getLogByTaskId(taskId)
            .takeIf { it.isNotEmpty() }
            ?: throw NoLogException("No history found for the task: $taskId")
    }
}