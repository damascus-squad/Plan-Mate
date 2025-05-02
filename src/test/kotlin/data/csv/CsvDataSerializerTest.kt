package data.csv

import com.google.common.truth.Truth.assertThat
import kotlinx.datetime.LocalDateTime
import logic.model.Admin
import logic.model.History
import logic.model.Project
import logic.model.State
import logic.model.Task
import data.csv.helpers.HistoryCsvHelper
import data.csv.helpers.ProjectCsvHelper
import data.csv.helpers.StateCsvHelper
import data.csv.helpers.TaskCsvHelper
import data.csv.helpers.UserCsvHelper
import org.damascus.data.csv.utils.CsvConstants.LIST_SEPARATOR
import org.damascus.logic.model.Role
import org.junit.jupiter.api.Test
import java.util.*

class CsvDataSerializerTest {
    val listOfUUIDs = List(10) { UUID.randomUUID() }
    val date1 = LocalDateTime.parse("2024-05-01T12:00:00")

    @Test
    fun `serializeUser should return csv entry when user is passed`() {
        // Given
        val user = Admin(
            id = listOfUUIDs[0],
            username = "Ameer",
            password = "SuperStrongPassword",
            role = Role.ADMIN
        )

        // When
        val result = UserCsvHelper.serializeUser(user)

        // Then
        val expected = listOf(
            listOfUUIDs[0],
            "Ameer",
            "SuperStrongPassword",
            Role.ADMIN
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
            creationDate = date1
        )

        // When
        val result = ProjectCsvHelper.serializeProject(project)

        // Then
        val expected = listOf(
            listOfUUIDs[0],
            "Project",
            listOf(listOfUUIDs[1], listOfUUIDs[2]).joinToString(LIST_SEPARATOR),
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
        val state = State(
            id = listOfUUIDs[0],
            name = "Backlog",
        )

        // When
        val result = StateCsvHelper.serializeState(state)

        // Then
        val expected = listOf(
            listOfUUIDs[0],
            "Backlog"
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
            actionType = "task",
            changedBy = listOfUUIDs[3],
            oldStateId = listOfUUIDs[4],
            newStateId = listOfUUIDs[5],
            timestamp = date1
        )

        // When
        val result = HistoryCsvHelper.serializeHistory(history)

        // Then
        val expected = listOf(
            listOfUUIDs[0],
            listOfUUIDs[1],
            listOfUUIDs[2],
            "task",
            listOfUUIDs[3],
            listOfUUIDs[4],
            listOfUUIDs[5],
            "2024-05-01T12:00"
        ).joinToString(",")

        assertThat(result).isEqualTo(expected)
    }

}