package data.csvDataHelper

import data.source.CsvHandlerImpl
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import logic.model.Mate
import logic.model.State
import logic.model.Task
import org.damascus.data.csv.FileDataParser
import org.damascus.data.csv.FileDataSerializer
import java.util.*

object CreateTaskHelper {
    fun createTask(
        id: UUID = UUID.randomUUID(),
        projectId: UUID = UUID.randomUUID(),
        title: String = "Test Task",
        description: String = "This is a test task",
        assignee: Mate? = null,
        state: State = State(UUID.randomUUID(), "TODO"),
        creationDate: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

    ): Task {
        return Task(
            id = id,
            projectId = projectId,
            title = title,
            description = description,
            assignee = assignee,
            state = state,
            creationDate = creationDate
        )
    }

    const val FILE_PATH_TASK = "test_assets/tasks.csv"

    fun buildHandlerTask(): CsvHandlerImpl<Task> {
        return CsvHandlerImpl(
            filePath = FILE_PATH_TASK,
            header = "id,projectId,title,description,assigneeId,stateId,creationDate",
            idSelector = { it.id.toString() },
            parser = FileDataParser::parseTask,
            serializer = { FileDataSerializer.serializeTask(it) }
        )
    }
}