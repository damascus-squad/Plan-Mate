package logic.model

import kotlinx.datetime.LocalDateTime
import java.util.UUID

data class History(
    val id: UUID,
    val projectId: UUID,
    val taskId: UUID,
    val actionType: String,
    val changedBy: UUID,
    val oldStateId: UUID,
    val newStateId: UUID,
    val timestamp: LocalDateTime
)