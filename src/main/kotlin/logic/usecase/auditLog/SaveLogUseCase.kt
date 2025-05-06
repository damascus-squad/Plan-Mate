package org.damascus.logic.usecase.AuditLog

import logic.repo.AuditLogsRepository
import org.damascus.logic.model.History

class SaveLogUseCase(
    private val auditLogsRepository: AuditLogsRepository
) {
    operator fun invoke(history: History) {
        auditLogsRepository.saveLog(history)
    }
}