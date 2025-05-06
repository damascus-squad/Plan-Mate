package data.repo

import logic.model.History
import logic.repo.AuditLogsRepository
import logic.repo.DataSource
import java.util.*

class AuditLogsRepositoryImpl(private val dataSource: DataSource<History>) : AuditLogsRepository {
    override fun saveLog(history: History) {
        dataSource.write(history)
    }

    override fun getLogsByProjectId(projectId: UUID): List<History> {
        return dataSource.read().filter { it.projectId == projectId }
    }

    override fun getLogsByTaskId(taskId: UUID): List<History> {
        return dataSource.read().filter { it.taskId == taskId }
    }
}