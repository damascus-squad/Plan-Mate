package logic.model

import kotlinx.datetime.LocalDateTime
import java.util.UUID

data class Project(
    val id: UUID,
    val name: String,
    val assignedMatesIds: MutableList<UUID>,
    val creationDate: LocalDateTime
)
