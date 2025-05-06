package logic.repo

import logic.model.History
import java.util.*

interface AuditLogsRepository {
    fun saveLog(history: History)
    fun getLogsByProjectId(projectId: UUID): List<History>
    fun getLogsByTaskId(taskId: UUID): List<History>
}