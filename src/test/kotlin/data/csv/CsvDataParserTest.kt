package data.csv

import com.google.common.truth.Truth.assertThat
import kotlinx.datetime.LocalDateTime
import logic.model.*
import org.damascus.data.csv.CsvParsingException
import org.damascus.data.csv.csvData.CsvDataHistory
import org.damascus.data.csv.csvData.CsvDataProject
import org.damascus.data.csv.csvData.CsvDataState
import org.damascus.data.csv.csvData.CsvDataTask
import org.damascus.data.csv.csvData.CsvDataUser
import org.damascus.logic.model.Role
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class CsvDataParserTest {
    val listOfUUIDs = List(10) { UUID.randomUUID() }
    val date1 = LocalDateTime.parse("2024-05-01T12:00:00")

    @Test
    fun `parseUser should return a valid mate when csv entry is a valid mate`() {
        // Given
        val csvEntry = "${listOfUUIDs[0]},alice,1234,mate"
        val expectedUser = Mate(
            listOfUUIDs[0], "alice", "1234",
            Role.MATE
        )

        // When
        val result = CsvDataUser.parseUser(csvEntry)

        // Then
        assertThat(result).isEqualTo(expectedUser)
    }

    @Test
    fun `parseUser should return a valid admin when csv entry is a valid admin`() {
        // Given
        val csvEntry = "${listOfUUIDs[0]},alice,1234,admin"
        val expectedUser = Admin(
            listOfUUIDs[0], "alice", "1234",
            Role.ADMIN
        )

        // When
        val result = CsvDataUser.parseUser(csvEntry)

        // Then
        assertThat(result).isEqualTo(expectedUser)
    }

    @Test
    fun `parseUser should throw CsvParsingException when csv entry is invalid`() {
        // Given
        val csvEntry = "${listOfUUIDs[0]},alice,1234,admin,ameer,ahmed"

        // When && Then
        assertThrows<CsvParsingException> {
            CsvDataUser.parseUser(csvEntry)
        }
    }

    @Test
    fun `parseProject should return a valid project when csv entry is a valid project`() {
        // Given
        val csvEntry = "${listOfUUIDs[0]},projectName,${listOfUUIDs[1]};${listOfUUIDs[2]};${listOfUUIDs[3]},${date1}"
        val expectedProject = Project(
            listOfUUIDs[0],
            "projectName",
            mutableListOf(listOfUUIDs[1], listOfUUIDs[2], listOfUUIDs[3]),
            date1
        )

        // When
        val result = CsvDataProject.parseProject(csvEntry)

        // Then
        assertThat(result).isEqualTo(expectedProject)
    }

    @Test
    fun `parseProject should ignore blank mates when csv entry contains blank entries`() {
        // Given
        val csvEntry =
            "${listOfUUIDs[0]},projectName,${listOfUUIDs[1]};;;${listOfUUIDs[2]};${listOfUUIDs[3]},${date1}"
        val expectedProject = Project(
            listOfUUIDs[0],
            "projectName",
            mutableListOf(
                listOfUUIDs[1], listOfUUIDs[2], listOfUUIDs[3]
            ),
            date1
        )

        // When
        val result = CsvDataProject.parseProject(csvEntry)

        // Then
        assertThat(result).isEqualTo(expectedProject)
    }

    @Test
    fun `parseProject should throw CsvParsingException when csv entry is invalid`() {
        // Given
        val csvEntry = "${listOfUUIDs[0]},projectName,${listOfUUIDs[1]},details,2024-05-01T12:00"

        // When && Then
        assertThrows<CsvParsingException> {
            CsvDataProject.parseProject(csvEntry)
        }
    }

    @Test
    fun `parseTask should return a valid task when csv entry is a valid task`() {
        // Given
        val csvEntry =
            "${listOfUUIDs[0]},${listOfUUIDs[1]},Super task,A task that never ends,${listOfUUIDs[2]},${listOfUUIDs[3]},${date1}"
        val expectedTask = Task(
            id = listOfUUIDs[0],
            projectId = listOfUUIDs[1],
            title = "Super task",
            description = "A task that never ends",
            assigneeId = listOfUUIDs[2],
            stateId = listOfUUIDs[3],
            creationDate = date1,
        )

        // When
        val result = CsvDataTask.parseTask(csvEntry)

        // Then
        assertThat(result).isEqualTo(expectedTask)
    }

    @Test
    fun `parseTask should return a task with null assigneeId when task is not assigned`() {
        // Given
        val csvEntry =
            "${listOfUUIDs[0]},${listOfUUIDs[1]},Super task,A task that never ends,,${listOfUUIDs[3]},${date1}"
        val expectedTask = Task(
            id = listOfUUIDs[0],
            projectId = listOfUUIDs[1],
            title = "Super task",
            description = "A task that never ends",
            assigneeId = null,
            stateId = listOfUUIDs[3],
            creationDate = date1,
        )

        // When
        val result = CsvDataTask.parseTask(csvEntry)

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
            CsvDataTask.parseTask(csvEntry)
        }
    }

    @Test
    fun `parseState should return a valid state when csv entry is a valid state`() {
        // Given
        val csvEntry = "${listOfUUIDs[0]},Backlog"

        val expectedState = State(
            id = listOfUUIDs[0],
            name = "Backlog",
        )

        // When
        val result = CsvDataState.parseState(csvEntry)

        // Then
        assertThat(result).isEqualTo(expectedState)
    }

    @Test
    fun `parseState should throw CsvParsingException when csv entry is invalid`() {
        // Given
        val csvEntry =  "${listOfUUIDs[0]},,,Backlog"

        // When && Then
        assertThrows<CsvParsingException> {
            CsvDataState.parseState(csvEntry)
        }
    }

    @Test
    fun `parseHistory should return a valid history when csv entry is a valid history`() {
        // Given
        val csvEntry = "${listOfUUIDs[0]},${listOfUUIDs[1]},${listOfUUIDs[2]},ChangedTask,${listOfUUIDs[3]},${listOfUUIDs[4]},${listOfUUIDs[5]},$date1"

        val expectedHistory = History(
            id = listOfUUIDs[0],
            projectId = listOfUUIDs[1],
            taskId = listOfUUIDs[2],
            actionType = "ChangedTask",
            changedBy = listOfUUIDs[3],
            oldStateId = listOfUUIDs[4],
            newStateId = listOfUUIDs[5],
            timestamp = date1,
        )

        // When
        val result = CsvDataHistory.parseHistory(csvEntry)

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
            CsvDataHistory.parseHistory(csvEntry)
        }
    }
}