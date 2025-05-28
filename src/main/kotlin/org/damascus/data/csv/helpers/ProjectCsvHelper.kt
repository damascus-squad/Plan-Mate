package org.damascus.data.csv.helpers

import org.damascus.data.csv.CsvParsingException
import org.damascus.data.csv.utils.CsvConstants
import org.damascus.data.dto.ProjectDTO
import java.util.*

object ProjectCsvHelper {

    private const val PROJECT_FIELD_COUNT = 5

    private enum class FieldPosition { ID, NAME, ASSIGNED_MATES_IDS, ALLOWED_STATES_IDS, CREATION_DATE }

    fun parseProject(line: String): ProjectDTO {
        val tokens = line.split(CsvConstants.COMMA_SEPARATOR)
        if (tokens.size != PROJECT_FIELD_COUNT) throw CsvParsingException("Invalid project line: $line")

        return ProjectDTO(
            id = tokens[FieldPosition.ID.ordinal].toCsvUuid(),
            name = tokens[FieldPosition.NAME.ordinal].trim(),
            assignedMatesIds = tokens[FieldPosition.ASSIGNED_MATES_IDS.ordinal].toCsvUuidMutableList(),
            allowedStatesIds = tokens[FieldPosition.ALLOWED_STATES_IDS.ordinal].toCsvUuidMutableList(),
            creationDate = tokens[FieldPosition.CREATION_DATE.ordinal].toCsvDate()
        )
    }

    fun serializeProject(project: ProjectDTO): String {
        val fields = Array(PROJECT_FIELD_COUNT) { "" }

        fields[FieldPosition.ID.ordinal] = project.id.toString()
        fields[FieldPosition.NAME.ordinal] = project.name
        fields[FieldPosition.ASSIGNED_MATES_IDS.ordinal] = project.assignedMatesIds.joinAsString()
        fields[FieldPosition.ALLOWED_STATES_IDS.ordinal] = project.allowedStatesIds.joinAsString()
        fields[FieldPosition.CREATION_DATE.ordinal] = project.creationDate.toString()

        return fields.joinToString(CsvConstants.COMMA_SEPARATOR)
    }

    private fun List<UUID>.joinAsString(): String = joinToString(CsvConstants.LIST_SEPARATOR) { it.toString() }
}