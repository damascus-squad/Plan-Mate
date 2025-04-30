package data.csvDataHelper

import logic.model.History
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import logic.model.State
import java.util.UUID


fun createHistory(
    id: UUID = UUID.randomUUID(),
    projectId: UUID = UUID.randomUUID(),
    entityId: UUID = UUID.randomUUID(),
    entityType: String = "TASK",
    changedBy: UUID = UUID.randomUUID(),
    oldState: State? = State(UUID.randomUUID(), "TODO"),
    newState: State? = State(UUID.randomUUID(), "IN_PROGRESS"),
    timestamp: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
): History {
    return History(
        id = id,
        projectId = projectId,
        taskId = entityId,
        entityType = entityType,
        changedBy = changedBy,
        oldState = oldState,
        newState = newState,
        timestamp = timestamp
    )
}
