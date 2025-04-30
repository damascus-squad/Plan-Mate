package data.csvDataHelper

import logic.model.Mate
import logic.model.Project
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.UUID

fun createProject(
    id: UUID = UUID.randomUUID(),
    name: String = "Test Project",
    assignedMates: MutableList<Mate> = mutableListOf(),
    creationDate: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

): Project {
    return Project(
        id = id,
        name = name,
        assignedMates = assignedMates,
        creationDate = creationDate
    )
}