package logic.model

import kotlinx.datetime.LocalDateTime
import java.util.*

data class Project(
    val id: UUID,
    val name: String,
    val assignedMatesIds: MutableList<UUID>,
    val allowedStatesIds: MutableList<UUID>,
    val creationDate: LocalDateTime
)
