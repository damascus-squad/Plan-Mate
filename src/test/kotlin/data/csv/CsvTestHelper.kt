package data.csv

import data.csv.helpers.HistoryCsvHelper
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import logic.model.ActionType
import logic.model.History
import java.util.*

object CsvTestHelper {

    fun createHistory(
        id: UUID = UUID.randomUUID(),
        projectId: UUID = UUID.randomUUID(),
        taskId: UUID = UUID.randomUUID(),
        actionType: ActionType = ActionType.TASK_STATE_CHANGED,
        userId: UUID = UUID.randomUUID(),
        oldStateId: UUID = UUID.randomUUID(),
        newStateId: UUID = UUID.randomUUID(),
        timestamp: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    ): History {
        return History(
            id = id,
            projectId = projectId,
            taskId = taskId,
            actionType = actionType,
            userId = userId,
            currentStateId = oldStateId,
            newStateId = newStateId,
            actionDate = timestamp,
        )
    }

    const val HISTORY_FILE_PATH = "test_assets/history.csv"

    fun getHistoryCsvHandler(): CsvDataSource<History> =
        CsvDataSource(
            filePath = HISTORY_FILE_PATH,
            generateHeader = { generateCsvHeader<History>() },
            extractId = { it.id },
            parser = HistoryCsvHelper::parseHistory,
            serializer = HistoryCsvHelper::serializeHistory
        )
}