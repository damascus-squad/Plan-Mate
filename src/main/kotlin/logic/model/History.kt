package logic.model

import kotlinx.datetime.LocalDateTime
import java.util.UUID

data class History(
    val id: UUID,
    val projectId: UUID,
    val entityId: UUID,
    val entityType: String,
    val changedBy: UUID,
    val oldState: State?,
    val newState: State?,
    val timestamp: LocalDateTime
)