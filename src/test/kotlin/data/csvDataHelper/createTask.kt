package data.csvDataHelper

import logic.model.Mate
import logic.model.State
import logic.model.Task
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.UUID

fun createTask(
    id: UUID = UUID.randomUUID(),
    projectId: UUID = UUID.randomUUID(),
    title: String = "Test Task",
    description: String = "This is a test task",
    assignee: Mate? = null,
    state: State = State(UUID.randomUUID(), "TODO"),
    creationDate: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

): Task {
    return Task(
        id = id,
        projectId = projectId,
        title = title,
        description = description,
        assignee = assignee,
        state = state,
        creationDate = creationDate
    )
}