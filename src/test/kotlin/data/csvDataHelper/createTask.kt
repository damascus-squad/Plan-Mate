package data.csvDataHelper

import data.model.Mate
import data.model.State
import data.model.Task
import java.time.LocalDateTime
import java.util.UUID

fun createTask(
    id: UUID = UUID.randomUUID(),
    projectId: UUID = UUID.randomUUID(),
    title: String = "Test Task",
    description: String = "This is a test task",
    assignee: Mate? = null,
    state: State = State(UUID.randomUUID(), "TODO"),
    creationDate: LocalDateTime = LocalDateTime.now()
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