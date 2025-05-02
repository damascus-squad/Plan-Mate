package logic.model

import kotlinx.datetime.LocalDateTime
import java.util.*

data class Task(
    val id: UUID,
    val projectId: UUID,
    val title: String,
    val description: String,
    var assigneeId: UUID?,
    val stateId: UUID,
    val creationDate: LocalDateTime
)