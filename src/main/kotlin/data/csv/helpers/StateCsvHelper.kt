package data.csv.helpers

import logic.model.State
import org.damascus.data.csv.CsvParsingException
import org.damascus.utils.Constants.SEPARATOR
import java.util.UUID

object StateCsvHelper {

    const val STATE_FIELD_COUNT = 2

    fun parseState(line: String): State {
        val tokens = line.split(SEPARATOR)
        if (tokens.size != STATE_FIELD_COUNT) throw CsvParsingException("Invalid state line: $line")

        val id = UUID.fromString(tokens[0].trim())
        val name = tokens[1].trim()
        return State(id, name)
    }

    fun serializeState(state: State): String {
        return listOf(state.id.toString(), state.name).joinToString(",")
    }

}