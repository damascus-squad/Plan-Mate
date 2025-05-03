package data.csv.helpers

import kotlinx.datetime.LocalDateTime
import org.damascus.data.csv.CsvParsingException
import org.damascus.data.csv.utils.CsvConstants.COMMA_SEPARATOR
import org.damascus.logic.model.ActionType
import org.damascus.logic.model.History
import java.util.*

object HistoryCsvHelper {

    const val HISTORY_FIELD_COUNT = 8


    fun parseHistory(line: String): History {
        val tokens = line.split(COMMA_SEPARATOR)
        if (tokens.size != HISTORY_FIELD_COUNT) throw CsvParsingException("Invalid history line: $line")

        return History(
            id = UUID.fromString(tokens[0].trim()),
            projectId = UUID.fromString(tokens[1].trim()),
            taskId = UUID.fromString(tokens[2].trim()),
            actionType = ActionType.entries[tokens[3].trim().toInt()],
            userId = UUID.fromString(tokens[4].trim()),
            currentStateId = UUID.fromString(tokens[5].trim()),
            newStateId = UUID.fromString(tokens[6].trim()),
            actionDate = LocalDateTime.parse(tokens[7].trim())
        )
    }

    fun serializeHistory(history: History): String {
        return listOf(
            history.id.toString(),
            history.projectId.toString(),
            history.taskId.toString(),
            history.actionType.ordinal.toString(),
            history.userId.toString(),
            history.currentStateId.toString(),
            history.newStateId.toString(),
            history.actionDate.toString()
        ).joinToString(COMMA_SEPARATOR)
    }
}