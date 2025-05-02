package data.csv

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import logic.model.*
import org.damascus.data.csv.CsvDataSource
import data.csv.helpers.HistoryCsvHelper
import data.csv.helpers.ProjectCsvHelper
import data.csv.helpers.StateCsvHelper
import data.csv.helpers.TaskCsvHelper
import data.csv.helpers.UserCsvHelper
import junit.runner.Version.id
import org.damascus.data.csv.generateCsvHeader
import org.damascus.logic.model.ActionType
import org.damascus.logic.model.Role
import java.util.*
import org.damascus.logic.model.History

object CsvTestHelper {

    fun createHistory(
        id: UUID = UUID.randomUUID(),
        projectId: UUID = UUID.randomUUID(),
        taskId: UUID = UUID.randomUUID(),
        actionType: ActionType = ActionType.TASK_STATE_CHANGED,
        changedBy: UUID = UUID.randomUUID(),
        currentState: State = State(UUID.randomUUID(), "TODO"),
        newState: State = State(UUID.randomUUID(), "In-progress"),
        actionDate: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    ): History {
        return History(
            id = id,
            projectId = projectId,
            taskId = taskId,
            actionType = actionType,
            userId = changedBy,
            currentState = currentState,
            newState = newState,
            actionDate = actionDate
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