package org.damascus.logic.feature.projectHistoryHandling

import org.damascus.logic.entities.ActionLog
import org.damascus.logic.repositories.HistoryRepository
import org.damascus.utiles.NoHistoryException
import java.util.UUID

class HistoryService(
    private val historyRepository: HistoryRepository
) {
    fun saveLog(actionLog: ActionLog){
        historyRepository.saveLog(actionLog)
    }

    fun getAllLogs() : List<ActionLog>{
        val logs = historyRepository.getAllLogs()
        if (logs.isEmpty()) {
            throw NoHistoryException("No logs found")
        }
        return logs
    }
    fun getLogByProjectId(projectId: UUID): List<ActionLog> {
        return historyRepository.getLogByProjectId(projectId)
    }
    fun getLogByTaskId(taskId: UUID): List<ActionLog> {
        return historyRepository.getLogByTaskId(taskId)
    }
}