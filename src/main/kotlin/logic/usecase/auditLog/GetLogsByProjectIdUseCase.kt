package org.damascus.logic.usecase.AuditLog

import logic.exception.NoLogsException
import logic.repo.AuditLogsRepository
import org.damascus.logic.model.History
import java.util.*

class GetLogsByProjectIdUseCase(
    private val auditLogsRepository: AuditLogsRepository
) {
    operator fun invoke(projectId: UUID): List<History> {
        return auditLogsRepository.getLogsByProjectId(projectId)
            .takeIf { it.isNotEmpty() }
            ?: throw NoLogsException("No history found for the project: $projectId")
    }
}