package org.damascus.logic.repo

import org.damascus.logic.model.History
import java.util.*

interface AuditLogsRepository {
    suspend fun saveLog(history: History)
    suspend fun getLogsByProjectId(projectId: UUID): List<History>
    suspend fun getLogsByTaskId(taskId: UUID): List<History>
}