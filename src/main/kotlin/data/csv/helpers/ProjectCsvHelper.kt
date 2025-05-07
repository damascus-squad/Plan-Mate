package data.csv.helpers

import data.csv.CsvParsingException
import data.csv.utils.CsvConstants.COMMA_SEPARATOR
import data.csv.utils.CsvConstants.LIST_SEPARATOR
import kotlinx.datetime.LocalDateTime
import logic.model.Project
import java.util.*

object ProjectCsvHelper {

    const val PROJECT_FIELD_COUNT = 4

    fun parseProject(line: String): Project {
        val tokens = line.split(COMMA_SEPARATOR)
        if (tokens.size != PROJECT_FIELD_COUNT) throw CsvParsingException("Invalid project line: $line")

        val id = UUID.fromString(tokens[0].trim())
        val name = tokens[1].trim()
        val assignedMatesIds = tokens[2].trim().split(LIST_SEPARATOR)
            .filter { it.isNotBlank() }
            .map { UUID.fromString(it) }
            .toMutableList()
        val creationDate = LocalDateTime.parse(tokens[3].trim())

        return Project(id, name, assignedMatesIds, creationDate)
    }

    fun serializeProject(project: Project): String {
        val matesString = project.assignedMatesIds.joinToString(LIST_SEPARATOR)
        return listOf(
            project.id.toString(),
            project.name,
            matesString,
            project.creationDate.toString()
        ).joinToString(COMMA_SEPARATOR)
    }
}