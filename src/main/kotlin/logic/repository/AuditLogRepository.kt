package logic.repository

import org.damascus.logic.model.History
import java.util.UUID

interface AuditLogRepository {
    fun saveLog(history: History)
    fun getLogsByProjectId(projectId: UUID): List<History>
    fun getLogByTaskId(taskId: UUID): List<History>
}