package org.damascus.data.repo

import org.damascus.logic.model.History
import org.damascus.logic.repo.AuditLogsRepository
import org.damascus.logic.repo.DataSource
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import java.util.*

@Single
class AuditLogsRepositoryImpl(
    @Named("historyDataSource")
    private val dataSource: DataSource<History>
) : AuditLogsRepository {
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