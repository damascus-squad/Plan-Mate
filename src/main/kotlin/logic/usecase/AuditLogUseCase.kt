package org.damascus.logic.usecase

import logic.exception.NoHistoryException
import logic.repo.AuditLogRepository
import org.damascus.logic.model.History
import java.util.*

class AuditLogUseCase(
    private val historyRepository: AuditLogRepository
) {
    fun saveLog(history: History) {
        historyRepository.saveLog(history)
    }

    fun getLogsByProjectId(projectId: UUID): List<History> {
        val logs = historyRepository.getLogsByProjectId(projectId)

        if (logs.isEmpty()) {
            throw NoHistoryException("No history found for project $projectId")
        }
        return logs
    }

    fun getLogByTaskId(taskId: UUID): List<History> {
        return historyRepository.getLogByTaskId(taskId)
    }
}