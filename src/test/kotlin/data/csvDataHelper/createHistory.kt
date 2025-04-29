package data.csvDataHelper

import data.model.History
import java.time.LocalDateTime
import java.util.UUID

fun createHistory(
    id: UUID = UUID.randomUUID(),
    projectID: UUID = UUID.randomUUID(),
    entityId: UUID = UUID.randomUUID(),
    entityType: String = "TASK",
    changedBy: UUID = UUID.randomUUID(),
    oldState: String = "TODO",
    newState: String = "In Progress",
    timestamp: LocalDateTime = LocalDateTime.now()
): History {
    return History(
        id = id,
        projectID = projectID,
        entityId = entityId,
        entityType = entityType,
        changedBy = changedBy,
        oldState = oldState,
        newState = newState,
        timestamp = timestamp
    )
}