package data.csv.helpers

import data.csv.CsvParsingException
import data.csv.utils.CsvConstants.COMMA_SEPARATOR
import logic.model.TaskState
import java.util.*

object StateCsvHelper {

    const val STATE_FIELD_COUNT = 2

    fun parseState(line: String): TaskState {
        val tokens = line.split(COMMA_SEPARATOR)
        if (tokens.size != STATE_FIELD_COUNT) throw CsvParsingException("Invalid state line: $line")

        val id = UUID.fromString(tokens[0].trim())
        val name = tokens[1].trim()
        return TaskState(id, name)
    }

    fun serializeState(taskState: TaskState): String {
        return listOf(taskState.id.toString(), taskState.name).joinToString(COMMA_SEPARATOR)
    }

}