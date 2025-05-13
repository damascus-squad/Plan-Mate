package data.csv

import com.google.common.truth.Truth.assertThat
import data.csv.helpers.*
import data.csv.utils.CsvConstants.LIST_SEPARATOR
import data.dto.UserDTO
import kotlinx.datetime.LocalDateTime
import logic.model.*
import org.junit.jupiter.api.Test
import java.util.*

class CsvDataSerializerTest {
    val listOfUUIDs = List(10) { UUID.randomUUID() }
    val date1 = LocalDateTime.parse("2024-05-01T12:00:00")

    @Test
    fun `serializeUser should return csv entry when user is passed`() {
        // Given
        val user = UserDTO(
            id = listOfUUIDs[0],
            hashedPassword = "SuperStrongPassword",
            username = "Ameer",
            userRole = UserRole.ADMIN
        )

        // When
        val result = UserCsvHelper.serializeUser(user)

        // Then
        val expected = listOf(
            listOfUUIDs[0],
            "SuperStrongPassword",
            "Ameer",
            UserRole.ADMIN
        ).joinToString(",")

        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `serializeProject should return csv entry when user is passed`() {
        // Given
        val project = Project(
            id = listOfUUIDs[0],
            name = "Project",
            assignedMatesIds = mutableListOf(listOfUUIDs[1], listOfUUIDs[2]),
            allowedStatesIds = mutableListOf(listOfUUIDs[3], listOfUUIDs[4]),
            creationDate = date1
        )

        // When
        val result = ProjectCsvHelper.serializeProject(project)

        // Then
        val expected = listOf(
            listOfUUIDs[0],
            "Project",
            listOf(listOfUUIDs[1], listOfUUIDs[2]).joinToString(LIST_SEPARATOR),
            listOf(listOfUUIDs[3], listOfUUIDs[4]).joinToString(LIST_SEPARATOR),
            "2024-05-01T12:00"
        ).joinToString(",")

        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `serializeTask should return csv entry when an assigned task is passed`() {
        // Given
        val task = Task(
            id = listOfUUIDs[0],
            projectId = listOfUUIDs[1],
            title = "EndlessTask",
            description = "Super duper task",
            assigneeId = listOfUUIDs[2],
            stateId = listOfUUIDs[3],
            creationDate = date1
        )

        // When
        val result = TaskCsvHelper.serializeTask(task)

        // Then
        val expected = listOf(
            listOfUUIDs[0],
            listOfUUIDs[1],
            "EndlessTask",
            "Super duper task",
            listOfUUIDs[2],
            listOfUUIDs[3],
            "2024-05-01T12:00"
        ).joinToString(",")

        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `serializeTask should return csv entry when a non-assigned task is passed`() {
        // Given
        val task = Task(
            id = listOfUUIDs[0],
            projectId = listOfUUIDs[1],
            title = "EndlessTask",
            description = "Super duper task",
            assigneeId = null,
            stateId = listOfUUIDs[3],
            creationDate = date1
        )

        // When
        val result = TaskCsvHelper.serializeTask(task)

        // Then
        val expected = listOf(
            listOfUUIDs[0],
            listOfUUIDs[1],
            "EndlessTask",
            "Super duper task",
            "",
            listOfUUIDs[3],
            "2024-05-01T12:00"
        ).joinToString(",")

        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `serializeState should return csv entry when state is passed`() {
        // Given
        val taskState = TaskState(
            id = listOfUUIDs[0],
            name = "Backlog",
            projectReferencesCount = 1,
        )

        // When
        val result = TaskStateCsvHelper.serializeTaskState(taskState)

        // Then
        val expected = listOf(
            listOfUUIDs[0],
            "Backlog",
            "1"
        ).joinToString(",")

        assertThat(result).isEqualTo(expected)
    }


    @Test
    fun `serializeHistory should return csv entry when history is passed`() {
        // Given
        val history = History(
            id = listOfUUIDs[0],
            projectId = listOfUUIDs[1],
            taskId = listOfUUIDs[2],
            actionType = ActionType.TASK_STATE_CHANGED,
            userId = listOfUUIDs[3],
            currentState = "TODO",
            newState = "IN PROGRESS",
            actionDate = date1
        )

        // When
        val result = HistoryCsvHelper.serializeHistory(history)

        // Then
        val expected = listOf(
            listOfUUIDs[0],
            listOfUUIDs[1],
            listOfUUIDs[2],
            ActionType.TASK_STATE_CHANGED.ordinal,
            listOfUUIDs[3],
            "TODO",
            "IN PROGRESS",
            "2024-05-01T12:00"
        ).joinToString(",")

        assertThat(result).isEqualTo(expected)
    }

}