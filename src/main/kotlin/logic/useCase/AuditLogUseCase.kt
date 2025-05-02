package org.damascus.logic.useCase


import logic.model.State
import org.damascus.logic.model.History
import org.damascus.utiles.NoHistoryException
import org.damascus.utiles.InvalidStateException
import logic.repository.AuditLogRepository
import org.damascus.data.DataSource
import java.util.UUID

class AuditLogUseCase(
    private val historyRepository: AuditLogRepository,
) {
    fun saveLog(history: History){
        val allowedStates = listOf("TODO", "In-progress", "Done")

        if (history.currentState !in allowedStates) {
            throw InvalidStateException("State ${history.currentState.name} is not allowed")
        }
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