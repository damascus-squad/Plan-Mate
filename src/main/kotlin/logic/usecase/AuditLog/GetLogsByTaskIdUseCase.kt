package org.damascus.logic.usecase.AuditLog

import logic.exception.NoLogException
import logic.repo.AuditLogsRepository
import org.damascus.logic.model.History
import java.util.*

class GetLogsByTaskIdUseCase(
    private val auditLogsRepository: AuditLogsRepository
) {
    operator fun invoke(taskId: UUID): List<History> {
        return auditLogsRepository.getLogsByTaskId(taskId)
            .takeIf { it.isNotEmpty() }
            ?: throw NoLogException("No history found for the task: $taskId")
    }
}