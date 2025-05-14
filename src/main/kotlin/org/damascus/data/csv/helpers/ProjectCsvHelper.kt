package org.damascus.data.csv.helpers

import org.damascus.data.csv.CsvParsingException
import kotlinx.datetime.LocalDateTime
import org.damascus.logic.model.Project
import org.damascus.data.csv.utils.CsvConstants
import java.util.*

object ProjectCsvHelper {

    private const val PROJECT_FIELD_COUNT = 5

    private enum class FieldPosition { ID, NAME, ASSIGNED_MATES_IDS, ALLOWED_STATES_IDS, CREATION_DATE }

    fun parseProject(line: String): Project {
        val tokens = line.split(CsvConstants.COMMA_SEPARATOR)
        if (tokens.size != PROJECT_FIELD_COUNT) throw CsvParsingException("Invalid project line: $line")

        return Project(
            id = parseUuid(tokens[FieldPosition.ID.ordinal]),
            name = parseName(tokens[FieldPosition.NAME.ordinal]),
            assignedMatesIds = parseUuidList(tokens[FieldPosition.ASSIGNED_MATES_IDS.ordinal]),
            allowedStatesIds = parseUuidList(tokens[FieldPosition.ALLOWED_STATES_IDS.ordinal]),
            creationDate = parseCreationDate(tokens[FieldPosition.CREATION_DATE.ordinal])
        )
    }

    private fun parseUuid(uuid: String): UUID =
        UUID.fromString(uuid.trim())

    private fun parseName(name: String): String =
        name.trim()

    private fun parseUuidList(uuidList: String): MutableList<UUID> =
        uuidList
            .trim()
            .split(CsvConstants.LIST_SEPARATOR)
            .filter { it.isNotBlank() }
            .map { UUID.fromString(it) }
            .toMutableList()

    private fun parseCreationDate(creationDate: String): LocalDateTime =
        LocalDateTime.parse(creationDate.trim())

    fun serializeProject(project: Project): String {
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