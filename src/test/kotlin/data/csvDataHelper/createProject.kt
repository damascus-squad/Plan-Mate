package data.csvDataHelper

import data.model.Mate
import data.model.Project
import java.time.LocalDateTime
import java.util.UUID

fun createProject(
    id: UUID = UUID.randomUUID(),
    name: String = "Test Project",
    assignedMates: MutableList<Mate> = mutableListOf(),
    creationDate: LocalDateTime = LocalDateTime.now()
): Project {
    return Project(
        id = id,
        name = name,
        assignedMates = assignedMates,
        creationDate = creationDate
    )
}