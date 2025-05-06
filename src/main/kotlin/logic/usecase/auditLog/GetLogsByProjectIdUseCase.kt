package logic.usecase.auditLog

import logic.exception.NoLogException
import logic.model.History
import logic.repo.AuditLogsRepository
import java.util.*

class GetLogsByProjectIdUseCase(
    private val auditLogsRepository: AuditLogsRepository
) {
    operator fun invoke(projectId: UUID): List<History> {
        return auditLogsRepository.getLogsByProjectId(projectId)
            .takeIf { it.isNotEmpty() }
            ?: throw NoLogException("No history found for the project: $projectId")
    }
}