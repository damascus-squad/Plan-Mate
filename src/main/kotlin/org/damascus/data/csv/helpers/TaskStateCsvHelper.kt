package org.damascus.data.csv.helpers

import org.damascus.data.csv.CsvParsingException
import org.damascus.data.csv.utils.CsvConstants
import org.damascus.data.dto.TaskStateDTO
import java.util.*

object TaskStateCsvHelper {

    private const val STATE_FIELD_COUNT = 3

    private enum class FieldPosition { ID, NAME, PROJECT_REFERENCES_COUNT }

    fun parseTaskState(line: String): TaskStateDTO {
        val tokens = line.split(CsvConstants.COMMA_SEPARATOR)
        if (tokens.size != STATE_FIELD_COUNT) throw CsvParsingException("Invalid state line: $line")

        return TaskStateDTO(
            id = parseUuid(tokens[FieldPosition.ID.ordinal]),
            name = parseName(tokens[FieldPosition.NAME.ordinal]),
            projectReferencesCount = parseInt(tokens[FieldPosition.PROJECT_REFERENCES_COUNT.ordinal])
        )
    }

    private fun parseUuid(uuid: String): UUID = UUID.fromString(uuid.trim())
    private fun parseName(name: String): String = name.trim()
    private fun parseInt(number: String): Int = number.toInt()

    fun serializeTaskState(taskState: TaskStateDTO): String {
        val fields = Array(STATE_FIELD_COUNT) { "" }

        fields[FieldPosition.ID.ordinal] = taskState.id.toString()
        fields[FieldPosition.NAME.ordinal] = taskState.name
        fields[FieldPosition.PROJECT_REFERENCES_COUNT.ordinal] = taskState.projectReferencesCount.toString()

        return fields.joinToString(CsvConstants.COMMA_SEPARATOR)
    }

}