package org.damascus.logic.feature.projectHistoryHandling


import org.damascus.logic.entities.ActionLog
import org.damascus.utiles.NoHistoryException
import org.damascus.utiles.InvalidStateException
import logic.repository.HistoryRepository
import java.util.UUID

class HistoryService(
    private val historyRepository: HistoryRepository
) {
    fun saveLog(actionLog: ActionLog){
        val allowedStates = listOf("TODO", "In-progress", "Done")

        if (actionLog.currentState.name !in allowedStates) {
            throw InvalidStateException("State ${actionLog.currentState.name} is not allowed")
        }
        historyRepository.saveLog(actionLog)
    }

    fun getLogsByProjectId(projectId: UUID): List<ActionLog> {
        val logs = historyRepository.getLogsByProjectId(projectId)

        if (logs.isEmpty()) {
            throw NoHistoryException("No history found for project $projectId")
        }
        return logs
    }

    fun getLogByTaskId(taskId: UUID): List<ActionLog> {
        return historyRepository.getLogByTaskId(taskId)
    }
}