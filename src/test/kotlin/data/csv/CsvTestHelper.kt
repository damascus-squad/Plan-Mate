package data.csv

import com.sun.beans.introspect.PropertyInfo
import jdk.javadoc.internal.doclets.formats.html.markup.HtmlStyle
import junit.runner.Version.id
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import logic.model.*
import org.damascus.data.csv.CsvDataParser
import org.damascus.data.csv.CsvDataSerializer
import org.damascus.data.csv.CsvDataSource
import org.damascus.data.csv.generateCsvHeader
import org.damascus.logic.model.Role
import java.util.*

object CsvTestHelper {

    fun createHistory(
        id: UUID = UUID.randomUUID(),
        projectId: UUID = UUID.randomUUID(),
        taskId: UUID = UUID.randomUUID(),
        actionType: String = "TASK",
        changedBy: UUID = UUID.randomUUID(),
        oldStateId: UUID = UUID.randomUUID(),
        newStateId: UUID = UUID.randomUUID(),
        timestamp: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    ): History {
        return History(
            id = id,
            projectId = projectId,
            taskId = taskId,
            actionType = actionType,
            changedBy = changedBy,
            oldStateId = oldStateId,
            newStateId = newStateId,
            timestamp = timestamp
        )
    }

    fun createUser(
        id: UUID = UUID.randomUUID(),
        username: String = "defaultUser",
        password: String = "defaultPass",
        role: Role = Role.MATE
    ): User {
        return when (role) {
            Role.ADMIN -> Admin(id = id, username = username, password = password, role = Role.ADMIN)
            Role.MATE -> Mate(id = id, username = username, password = password, role = Role.MATE)
        }
    }

    fun createMate(
        id: UUID = UUID.randomUUID(),
        username: String = "defaultUser",
        password: String = "defaultPass"
    ): Mate {
        return Mate(id = id, username = username, password = password, role = Role.MATE)
    }

    fun createTask(
        id: UUID = UUID.randomUUID(),
        projectId: UUID = UUID.randomUUID(),
        title: String = "Test Task",
        description: String = "This is a test task",
        assigneeId: UUID? = null,
        stateId: UUID = UUID.randomUUID(),
        creationDate: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

    ): Task {
        return Task(
            id = id,
            projectId = projectId,
            title = title,
            description = description,
            assigneeId = assigneeId,
            stateId = stateId,
            creationDate = creationDate
        )
    }

    fun createState(
        id: UUID = UUID.randomUUID(),
        name: String = "Test State"
    ) = State(
        id = id,
        name = name
    )

    fun createProject(
        id: UUID = UUID.randomUUID(),
        name: String = "Test Project",
        assignedMates: MutableList<UUID> = mutableListOf(),
        creationDate: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

    ): Project {
        return Project(
            id = id,
            name = name,
            assignedMatesIds = assignedMates,
            creationDate = creationDate
        )
    }

    const val PROJECT_FILE_PATH = "test_assets/projects.csv"
    const val STATE_FILE_PATH = "test_assets/states.csv"
    const val TASK_FILE_PATH = "test_assets/tasks.csv"
    const val USER_FILE_PATH = "test_assets/users.csv"
    const val HISTORY_FILE_PATH = "test_assets/history.csv"

    fun getHistoryCsvHandler(): CsvDataSource<History> =
        CsvDataSource(
            filePath = HISTORY_FILE_PATH,
            generateHeader = { generateCsvHeader<History>() },
            extractId = { it.id },
            parser = CsvDataParser::parseHistory,
            serializer = CsvDataSerializer::serializeHistory
        )

    fun getProjectCsvHandler(): CsvDataSource<Project> =
        CsvDataSource(
            filePath = PROJECT_FILE_PATH,
            generateHeader = { generateCsvHeader<Project>() },
            extractId = { it.id },
            parser = CsvDataParser::parseProject,
            serializer = CsvDataSerializer::serializeProject
        )

    fun getUserCsvHandler(): CsvDataSource<User> =
        CsvDataSource(
            filePath = USER_FILE_PATH,
            generateHeader = { generateCsvHeader<User>() },
            extractId = { it.id },
            parser = CsvDataParser::parseUser,
            serializer = CsvDataSerializer::serializeUser
        )

    fun getTaskCsvHandler(): CsvDataSource<Task> =
        CsvDataSource(
            filePath = TASK_FILE_PATH,
            generateHeader = { generateCsvHeader<Task>() },
            extractId = { it.id },
            parser = CsvDataParser::parseTask,
            serializer = CsvDataSerializer::serializeTask
        )

    fun getStateCsvHandler(): CsvDataSource<State> =
        CsvDataSource(
            filePath = STATE_FILE_PATH,
            generateHeader = { generateCsvHeader<State>() },
            extractId = { it.id },
            parser = CsvDataParser::parseState,
            serializer = CsvDataSerializer::serializeState
        )
}