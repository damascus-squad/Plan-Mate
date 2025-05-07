package logic.usecase.auditLog

import logic.model.History
import logic.repo.AuditLogsRepository

class SaveLogUseCase(
    private val auditLogsRepository: AuditLogsRepository
) {
    operator fun invoke(history: History) {
        auditLogsRepository.saveLog(history)
    }
}