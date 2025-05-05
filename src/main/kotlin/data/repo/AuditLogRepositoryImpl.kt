package org.damascus.data.repo

import logic.repo.AuditLogsRepository
import logic.repo.DataSource
import org.damascus.logic.model.History
import java.util.*

class AuditLogRepositoryImpl(private val dataSource: DataSource<History>) : AuditLogsRepository {
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