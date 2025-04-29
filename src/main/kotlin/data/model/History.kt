package data.model

import java.time.LocalDateTime
import java.util.UUID

data class History(
    val id: UUID = UUID.randomUUID(),
    val projectID: UUID,
    val entityId: UUID,
    val entityType: String,
    val changedBy: UUID,
    val oldState: String?,
    val newState: String?,
    val timestamp: LocalDateTime = LocalDateTime.now()
)