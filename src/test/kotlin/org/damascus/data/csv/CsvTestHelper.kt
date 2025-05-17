package org.damascus.data.csv

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.damascus.data.csv.helpers.HistoryCsvHelper
import org.damascus.data.dto.HistoryLogDTO
import org.damascus.logic.model.ActionType
import java.util.*

object CsvTestHelper {

    fun createHistory(
        id: UUID = UUID.randomUUID(),
        projectId: UUID = UUID.randomUUID(),
        taskId: UUID = UUID.randomUUID(),
        actionType: ActionType = ActionType.TASK_STATE_CHANGED,
        userId: UUID = UUID.randomUUID(),
        oldStateId: String = "TODO",
        newStateId: String = "IN PROGRESS",
        timestamp: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    ) = HistoryLogDTO(
        id = id,
        projectId = projectId,
        taskId = taskId,
        actionType = actionType,
        userId = userId,
        currentState = oldStateId,
        newState = newStateId,
        actionDate = timestamp,
    )

    const val HISTORY_FILE_PATH = "assetsTest/history.csv"

    fun getHistoryCsvHandler(): CsvDataSource<HistoryLogDTO> =
        CsvDataSource(
            filePath = HISTORY_FILE_PATH,
            generateHeader = { generateCsvHeader<HistoryLogDTO>() },
            extractId = { it.id },
            parser = HistoryCsvHelper::parseHistory,
            serializer = HistoryCsvHelper::serializeHistory
        )
}