package model

import kotlinx.datetime.LocalDateTime
import java.util.UUID

data class Project(
    val id: UUID,
    val name: String,
    val assignedMates: MutableList<Mate>,
    val creationDate: LocalDateTime
)
