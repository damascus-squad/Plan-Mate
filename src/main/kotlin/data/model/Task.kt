package data.model

import java.time.LocalDateTime
import java.util.UUID

data class Task(
    val id: UUID = UUID.randomUUID(),
    val projectId: UUID,
    val title: String,
    val description: String,
    var assignee: Mate? = null,
    val state: State,
    val creationDate: LocalDateTime
)
