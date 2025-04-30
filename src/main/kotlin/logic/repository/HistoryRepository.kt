package org.damascus.logic.repositories

import org.damascus.logic.entities.ActionLog
import java.util.UUID


interface HistoryRepository {
    fun saveLog(actionLog: ActionLog)

    fun getLogByProjectId(projectId: UUID): List<ActionLog>

    fun getLogByTaskId(taskId: UUID): List<ActionLog>

    fun getAllLogs(): List<ActionLog>
}