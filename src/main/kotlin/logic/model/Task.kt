package logic.model

import kotlinx.datetime.LocalDateTime
import java.util.UUID

data class Task(
    val id: UUID,
    val projectId: UUID,
    val title: String,
    val description: String,
    var assignee: Mate?,
    val state: State,
    val creationDate: LocalDateTime)
