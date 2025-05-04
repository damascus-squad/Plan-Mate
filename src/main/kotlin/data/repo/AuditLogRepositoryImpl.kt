package org.damascus.data.repo

import logic.repo.AuditLogRepository
import logic.repo.DataSource
import org.damascus.logic.model.History
import java.util.*

class AuditLogRepositoryImpl(private val dataSource: DataSource<History>): AuditLogRepository{
    override fun saveLog(history: History){
        dataSource.write(history) // or .save(history) depending on the function name
    }

    override fun getLogByProjectId(projectId: UUID): List<History>{
        return dataSource.read().filter {it.projectId == projectId}
    }

    override fun getLogByTaskId(taskId: UUID): List<History>{
        return dataSource.read().filter {it.taskId == taskId}
    }
}