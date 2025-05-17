package org.damascus.data.csv.helpers

import org.damascus.data.csv.CsvParsingException
import kotlinx.datetime.LocalDateTime
import org.damascus.logic.model.ActionType
import org.damascus.logic.model.History
import org.damascus.data.csv.utils.CsvConstants
import org.damascus.data.dto.HistoryLogDTO
import java.util.*

object HistoryCsvHelper {

    private const val HISTORY_FIELD_COUNT = 8

    fun parseHistory(line: String): HistoryLogDTO {
        val tokens = line.split(CsvConstants.COMMA_SEPARATOR)
        if (tokens.size != HISTORY_FIELD_COUNT) throw CsvParsingException("Invalid history line: $line")

        return HistoryLogDTO(
            id = UUID.fromString(tokens[0].trim()),
            projectId = UUID.fromString(tokens[1].trim()),
            taskId = UUID.fromString(tokens[2].trim()),
            actionType = ActionType.entries[tokens[3].trim().toInt()],
            userId = UUID.fromString(tokens[4].trim()),
            currentState = tokens[5].trim(),
            newState = tokens[6].trim(),
            actionDate = LocalDateTime.parse(tokens[7].trim())
        )
    }

    fun serializeHistory(history: HistoryLogDTO): String {
        return listOf(
            history.id.toString(),
            history.projectId.toString(),
            history.taskId.toString(),
            history.actionType.ordinal.toString(),
            history.userId.toString(),
            history.currentState,
            history.newState,
            history.actionDate.toString()
        ).joinToString(CsvConstants.COMMA_SEPARATOR)
    }
}