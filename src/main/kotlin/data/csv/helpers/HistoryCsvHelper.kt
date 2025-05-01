package data.csv.helpers

import kotlinx.datetime.LocalDateTime
import logic.model.History
import org.damascus.data.csv.CsvParsingException
import org.damascus.data.csv.utils.CsvConstants.COMMA_SEPARATOR
import java.util.UUID

object HistoryCsvHelper {

    const val HISTORY_FIELD_COUNT = 8

    fun parseHistory(line: String): History {
        val tokens = line.split(COMMA_SEPARATOR)
        if (tokens.size != HISTORY_FIELD_COUNT) throw CsvParsingException("Invalid history line: $line")

        return History(
            id = UUID.fromString(tokens[0].trim()),
            projectId = UUID.fromString(tokens[1].trim()),
            taskId = UUID.fromString(tokens[2].trim()),
            actionType = tokens[3].trim(),
            changedBy = UUID.fromString(tokens[4].trim()),
            oldStateId = UUID.fromString(tokens[5].trim()),
            newStateId = UUID.fromString(tokens[6].trim()),
            timestamp = LocalDateTime.parse(tokens[7].trim())
        )
    }

    fun serializeHistory(history: History): String {
        return listOf(
            history.id.toString(),
            history.projectId.toString(),
            history.taskId.toString(),
            history.actionType,
            history.changedBy.toString(),
            history.oldStateId.toString() ,
            history.newStateId.toString(),
            history.timestamp.toString()
        ).joinToString(COMMA_SEPARATOR)
    }
}