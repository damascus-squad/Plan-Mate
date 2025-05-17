package org.damascus.data.repo

import org.damascus.data.dto.HistoryLogDTO
import org.damascus.data.mapper.toDto
import org.damascus.data.mapper.toModel
import org.damascus.logic.model.History
import org.damascus.logic.repo.AuditLogsRepository
import org.damascus.logic.repo.DataSource
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import java.util.*

@Single
class AuditLogsRepositoryImpl(
    @Named("historyDataSource")
    private val dataSource: DataSource<HistoryLogDTO>
) : AuditLogsRepository {
    override suspend fun saveLog(history: History) {
        dataSource.write(history.toDto())
    }

    override suspend fun getLogsByProjectId(projectId: UUID): List<History> {
        return dataSource.read().filter { it.projectId == projectId }.map { it.toModel() }
    }

    override suspend fun getLogsByTaskId(taskId: UUID): List<History> {
        return dataSource.read().filter { it.taskId == taskId }.map { it.toModel() }
    }
}