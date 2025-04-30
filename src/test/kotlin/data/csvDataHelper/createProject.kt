package data.csvDataHelper

import data.source.CsvHandlerImpl
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import logic.model.Mate
import logic.model.Project
import org.damascus.data.csv.FileDataParser
import org.damascus.data.csv.FileDataSerializer
import java.util.*

object CreateProjectHelper {
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

    const val FILE_PATH_PROJECT = "test_assets/projects.csv"

    fun buildHandlerProject(): CsvHandlerImpl<Project> {
        return CsvHandlerImpl(
            filePath = FILE_PATH_PROJECT,
            header = "id,name,assignedMates,creationDate",
            idSelector = { it.id.toString() },
            parser = FileDataParser::parseProject,
            serializer = FileDataSerializer::serializeProject
        )
    }
}