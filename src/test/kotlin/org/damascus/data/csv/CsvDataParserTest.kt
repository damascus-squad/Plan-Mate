package org.damascus.data.csv

import com.google.common.truth.Truth.assertThat
import kotlinx.datetime.LocalDateTime
import org.damascus.data.csv.helpers.*
import org.damascus.data.dto.*
import org.damascus.logic.model.ActionType
import org.damascus.logic.model.UserRole
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class CsvDataParserTest {
    private val listOfUUIDs = List(10) { UUID.randomUUID() }
    private val currentState = "TODO"
    private val newState = "IN Progress"
    private val date1 = LocalDateTime.parse("2024-05-01T12:00:00")

    @Test
    fun `parseUser should return a valid mate when csv entry is a valid mate`() {
        // Given
        val csvEntry = "${listOfUUIDs[0]},alice,1234,1"
        val expectedUser = UserDTO(
            listOfUUIDs[0], "alice", "1234",
            UserRole.MATE
        )

        // When
        val result = UserCsvHelper.parseUser(csvEntry)

        // Then
        assertThat(result).isEqualTo(expectedUser)
    }

    @Test
    fun `parseUser should return a valid admin when csv entry is a valid admin`() {
        // Given
        val csvEntry = "${listOfUUIDs[0]},alice,1234,0"
        val expectedUser = UserDTO(
            listOfUUIDs[0], "alice", "1234",
            UserRole.ADMIN
        )

        // When
        val result = UserCsvHelper.parseUser(csvEntry)

        // Then
        assertThat(result).isEqualTo(expectedUser)
    }

    @Test
    fun `parseUser should throw CsvParsingException when csv entry is invalid`() {
        // Given
        val csvEntry = "${listOfUUIDs[0]},alice,1234,admin,ameer,ahmed"

        // When && Then
        assertThrows<CsvParsingException> {
            UserCsvHelper.parseUser(csvEntry)
        }
    }

    @Test
    fun `parseProject should return a valid project when csv entry is a valid project`() {
        // Given
        val csvEntry =
            "${listOfUUIDs[0]},projectName,${listOfUUIDs[1]};${listOfUUIDs[2]};${listOfUUIDs[3]},${listOfUUIDs[4]};${listOfUUIDs[5]},${date1}"
        val expectedProject = ProjectDTO(
            listOfUUIDs[0],
            "projectName",
            mutableListOf(listOfUUIDs[1], listOfUUIDs[2], listOfUUIDs[3]),
            mutableListOf(listOfUUIDs[4], listOfUUIDs[5]),
            date1
        )

        // When
        val result = ProjectCsvHelper.parseProject(csvEntry)

        // Then
        assertThat(result).isEqualTo(expectedProject)
    }

    @Test
    fun `parseProject should ignore blank mates when csv entry contains blank entries`() {
        // Given
        val csvEntry =
            "${listOfUUIDs[0]},projectName,${listOfUUIDs[1]};;;${listOfUUIDs[2]};${listOfUUIDs[3]},${listOfUUIDs[4]};${listOfUUIDs[5]},${date1}"
        val expectedProject = ProjectDTO(
            listOfUUIDs[0],
            "projectName",
            mutableListOf(
                listOfUUIDs[1], listOfUUIDs[2], listOfUUIDs[3]
            ),
            mutableListOf(listOfUUIDs[4], listOfUUIDs[5]),
            date1
        )

        // When
        val result = ProjectCsvHelper.parseProject(csvEntry)

        // Then
        assertThat(result).isEqualTo(expectedProject)
    }

    @Test
    fun `parseProject should throw CsvParsingException when csv entry is invalid`() {
        // Given
        val csvEntry = "${listOfUUIDs[0]},projectName,,,${listOfUUIDs[1]},details,2024-05-01T12:00"

        // When && Then
        assertThrows<CsvParsingException> {
            ProjectCsvHelper.parseProject(csvEntry)
        }
    }

    @Test
    fun `parseTask should return a valid task when csv entry is a valid task`() {
        // Given
        val csvEntry =
            "${listOfUUIDs[0]},${listOfUUIDs[1]},Super task,A task that never ends,${listOfUUIDs[2]},${listOfUUIDs[3]},${date1}"
        val expectedTask = TaskDTO(
            id = listOfUUIDs[0],
            projectId = listOfUUIDs[1],
            title = "Super task",
            description = "A task that never ends",
            assigneeId = listOfUUIDs[2],
            stateId = listOfUUIDs[3],
            creationDate = date1,
        )

        // When
        val result = TaskCsvHelper.parseTask(csvEntry)

        // Then
        assertThat(result).isEqualTo(expectedTask)
    }

    @Test
    fun `parseTask should return a task with null assigneeId when task is not assigned`() {
        // Given
        val csvEntry =
            "${listOfUUIDs[0]},${listOfUUIDs[1]},Super task,A task that never ends,,${listOfUUIDs[3]},${date1}"
        val expectedTask = TaskDTO(
            id = listOfUUIDs[0],
            projectId = listOfUUIDs[1],
            title = "Super task",
            description = "A task that never ends",
            assigneeId = null,
            stateId = listOfUUIDs[3],
            creationDate = date1,
        )

        // When
        val result = TaskCsvHelper.parseTask(csvEntry)

        // Then
        assertThat(result).isEqualTo(expectedTask)
    }

    @Test
    fun `parseTask should throw CsvParsingException when csv entry is invalid`() {
        // Given
        val csvEntry =
            "${listOfUUIDs[0]},${listOfUUIDs[1]},Super Task,A task, ,${listOfUUIDs[2]},${listOfUUIDs[3]},${date1}"

        // When && Then
        assertThrows<CsvParsingException> {
            TaskCsvHelper.parseTask(csvEntry)
        }
    }

    @Test
    fun `parseState should return a valid state when csv entry is a valid state`() {
        // Given
        val csvEntry = "${listOfUUIDs[0]},Backlog,1"

        val expectedTaskState = TaskStateDTO(
            id = listOfUUIDs[0],
            name = "Backlog",
            projectReferencesCount = 1
        )

        // When
        val result = TaskStateCsvHelper.parseTaskState(csvEntry)

        // Then
        assertThat(result).isEqualTo(expectedTaskState)
    }

    @Test
    fun `parseState should throw CsvParsingException when csv entry is invalid`() {
        // Given
        val csvEntry = "${listOfUUIDs[0]},,,Backlog,1"

        // When && Then
        assertThrows<CsvParsingException> {
            TaskStateCsvHelper.parseTaskState(csvEntry)
        }
    }

    @Test
    fun `parseHistory should return a valid history when csv entry is a valid history`() {
        // Given
        val csvEntry =
            "${listOfUUIDs[0]},${listOfUUIDs[1]},${listOfUUIDs[2]},${ActionType.TASK_STATE_CHANGED.ordinal},${listOfUUIDs[3]}, $currentState, $newState  ,$date1"

        val expectedHistory = HistoryLogDTO(
            id = listOfUUIDs[0],
            projectId = listOfUUIDs[1],
            taskId = listOfUUIDs[2],
            actionType = ActionType.TASK_STATE_CHANGED,
            userId = listOfUUIDs[3],
            currentState = currentState,
            newState = newState,
            actionDate = date1
        )

        // When
        val result = HistoryCsvHelper.parseHistory(csvEntry)

        // Then
        assertThat(result).isEqualTo(expectedHistory)
    }

    @Test
    fun `parseHistory should throw CsvParsingException when csv entry is invalid`() {
        // Given
        val csvEntry =
            "${listOfUUIDs[0]},${listOfUUIDs[1]},,,ChangedTask,${listOfUUIDs[3]},${listOfUUIDs[4]},${listOfUUIDs[5]},$date1"

        // When && Then
        assertThrows<CsvParsingException> {
            HistoryCsvHelper.parseHistory(csvEntry)
        }
    }
}