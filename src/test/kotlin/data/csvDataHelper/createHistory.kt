package data.csvDataHelper

import data.source.CsvHandlerImpl
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import logic.model.History
import logic.model.State
import org.damascus.data.csv.FileDataParser
import org.damascus.data.csv.FileDataSerializer
import java.util.*

object CreateHistoryHelper {

    fun createHistory(
        id: UUID = UUID.randomUUID(),
        projectId: UUID = UUID.randomUUID(),
        taskId: UUID = UUID.randomUUID(),
        actionType: String = "TASK",
        changedBy: UUID = UUID.randomUUID(),
        oldState: State? = State(UUID.randomUUID(), "TODO"),
        newState: State = State(UUID.randomUUID(), "IN_PROGRESS"),
        timestamp: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    ): History {
        return History(
            id = id,
            projectId = projectId,
            taskId = taskId,
            actionType = actionType,
            changedBy = changedBy,
            oldState = oldState,
            newState = newState,
            timestamp = timestamp
        )
    }

    const val FILE_PATH_HISTORY = "test_assets/history.csv"

    fun buildHandlerHistory(): CsvHandlerImpl<History> {
        return CsvHandlerImpl(
            filePath = FILE_PATH_HISTORY,
            header = "id,projectID,taskId,actionType,changedBy,oldState,newState,timestamp",
            idSelector = { it.id.toString() },
            parser = FileDataParser::parseHistory,
            serializer = FileDataSerializer::serializeHistory
        )
    }
}
