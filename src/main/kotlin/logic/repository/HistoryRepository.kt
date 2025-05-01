package org.damascus.logic.repositories

import org.damascus.logic.entities.ActionLog
import java.util.UUID

interface HistoryRepository {
    fun saveLog(actionLog: ActionLog)

    fun getLogsByProjectId(projectId: UUID): List<ActionLog>

    fun getLogByTaskId(taskId: UUID): List<ActionLog>

}