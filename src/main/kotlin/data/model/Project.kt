package data.model

import java.time.LocalDateTime
import java.util.UUID

data class Project(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val assignedMates: MutableList<Mate>,
    val creationDate: LocalDateTime
)
