package org.damascus.data.csv.helpers

import org.damascus.data.csv.CsvParsingException
import org.damascus.data.csv.utils.CsvConstants
import org.damascus.data.dto.HistoryLogDTO
import org.damascus.logic.model.ActionType

object HistoryCsvHelper {

    private const val HISTORY_FIELD_COUNT = 8

    private enum class FieldPosition {
        ID,
        PROJECT_ID,
        TASK_ID,
        ACTION_TYPE,
        USER_ID,
        CURRENT_STATE,
        NEW_STATE,
        ACTION_DATE
    }

    fun parseHistory(line: String): HistoryLogDTO {
        val tokens = line.split(CsvConstants.COMMA_SEPARATOR)
        if (tokens.size != HISTORY_FIELD_COUNT) throw CsvParsingException("Invalid history line: $line")

        return HistoryLogDTO(
            id = tokens[FieldPosition.ID.ordinal].trim().toCsvUuid(),
            projectId = tokens[FieldPosition.PROJECT_ID.ordinal].trim().toCsvUuid(),
            taskId = tokens[FieldPosition.TASK_ID.ordinal].trim().toCsvUuid(),
            actionType = ActionType.entries[tokens[FieldPosition.ACTION_TYPE.ordinal].toCsvInt()],
            userId = tokens[FieldPosition.USER_ID.ordinal].trim().toCsvUuid(),
            currentState = tokens[FieldPosition.CURRENT_STATE.ordinal].trim(),
            newState = tokens[FieldPosition.NEW_STATE.ordinal].trim(),
            actionDate = tokens[FieldPosition.ACTION_DATE.ordinal].trim().toCsvDate()
        )
    }

    fun serializeHistory(history: HistoryLogDTO): String {
        val fields = Array(HISTORY_FIELD_COUNT) { "" }

        fields[FieldPosition.ID.ordinal] = history.id.toString()
        fields[FieldPosition.PROJECT_ID.ordinal] = history.projectId.toString()
        fields[FieldPosition.TASK_ID.ordinal] = history.taskId.toString()
        fields[FieldPosition.ACTION_TYPE.ordinal] = history.actionType.ordinal.toString()
        fields[FieldPosition.USER_ID.ordinal] = history.userId.toString()
        fields[FieldPosition.CURRENT_STATE.ordinal] = history.currentState.toString()
        fields[FieldPosition.NEW_STATE.ordinal] = history.newState.toString()
        fields[FieldPosition.ACTION_DATE.ordinal] = history.actionDate.toString()

        return fields.joinToString(CsvConstants.COMMA_SEPARATOR)
    }
}