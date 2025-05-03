package logic.repo

import org.damascus.logic.model.History
import java.util.*

interface AuditLogRepository {
    fun saveLog(history: History)

    fun getLogsByProjectId(projectId: UUID): List<History>

    fun getLogByTaskId(taskId: UUID): List<History>
}