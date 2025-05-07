package data.csv.helpers

import data.csv.CsvParsingException
import data.csv.utils.CsvConstants.COMMA_SEPARATOR
import logic.model.TaskState
import java.util.*

object TaskStateCsvHelper {

    private const val STATE_FIELD_COUNT = 3

    private enum class FieldPosition { ID, NAME, PROJECT_REFERENCES_COUNT }

    fun parseTaskState(line: String): TaskState {
        val tokens = line.split(COMMA_SEPARATOR)
        if (tokens.size != STATE_FIELD_COUNT) throw CsvParsingException("Invalid state line: $line")

        return TaskState(
            id = parseUuid(tokens[FieldPosition.ID.ordinal]),
            name = parseName(tokens[FieldPosition.ID.ordinal]),
            projectReferencesCount = parseInt(tokens[FieldPosition.PROJECT_REFERENCES_COUNT.ordinal])
        )
    }

    private fun parseUuid(uuid: String): UUID = UUID.fromString(uuid.trim())
    private fun parseName(name: String): String = name.trim()
    private fun parseInt(number: String): Int = number.toInt()

    fun serializeTaskState(taskState: TaskState): String {
        val fields = Array(STATE_FIELD_COUNT) { "" }

        fields[FieldPosition.ID.ordinal] = taskState.id.toString()
        fields[FieldPosition.NAME.ordinal] = taskState.name
        fields[FieldPosition.PROJECT_REFERENCES_COUNT.ordinal] = taskState.projectReferencesCount.toString()

        return fields.joinToString(COMMA_SEPARATOR)
    }

}