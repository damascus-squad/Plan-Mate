package org.damascus.data.repo

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.damascus.data.csv.CsvDataSource
import org.damascus.logic.exception.TaskAlreadyExistsException
import org.damascus.logic.exception.TaskNotFoundException
import org.damascus.logic.model.Task
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import java.util.*
import kotlin.test.Test

class TaskRepositoryImplTest {
    private lateinit var dataSource: CsvDataSource<Task>
    private lateinit var repository: TaskRepositoryImpl

    @BeforeEach
    fun setup() {
        dataSource = mockk(relaxed = true)
        repository = TaskRepositoryImpl(dataSource)
    }

    @Test
    fun `create should add new task if not exists`() = runTest {
        // Given
        coEvery { dataSource.read() } returns emptyList()

        // When
        repository.create(sampleTask)

        // Then
        coVerify(exactly = 1) { dataSource.write(sampleTask) }
    }

    @Test
    fun `create should throw exception when task already exists`() = runTest {
        // Given
        coEvery { dataSource.read() } returns listOf(sampleTask)

        // When && Than
        assertThrows<TaskAlreadyExistsException> {
            repository.create(sampleTask)
        }
    }

    @Test
    fun `update should replace existing task`() = runTest {
        // Given
        coEvery { dataSource.read() } returns listOf(sampleTask)
        val updatedTask = sampleTask.copy(title = "Updated Title")

        // When
        repository.update(sampleTask.id, updatedTask)

        // Then
        coVerify(exactly = 1) { dataSource.update(sampleTask.id, updatedTask) }
    }

    @Test
    fun `update should throw exception when task does not exist`() = runTest {
        // Given
        coEvery { dataSource.read() } returns emptyList()

        // When && That
        assertThrows<TaskNotFoundException> {
            repository.update(sampleTask.id, sampleTask)
        }
    }

    @Test
    fun `delete should remove existing task`() = runTest {
        // Given
        coEvery { dataSource.read() } returns listOf(sampleTask)

        // When
        repository.delete(sampleTask.id)

        //Then
        coVerify(exactly = 1) { dataSource.delete(sampleTask.id) }
    }

    @Test
    fun `delete should throw exception when task does not exist`() = runTest {
        // Given
        coEvery { dataSource.read() } returns emptyList()

        // When && Then
        assertThrows<TaskNotFoundException> {
            repository.delete(sampleTask.id)
        }
    }

    @Test
    fun `get should return task when it exists`() = runTest {
        // Given
        coEvery { dataSource.read() } returns listOf(sampleTask)

        // When
        val result = repository.get(sampleTask.id)

        // Then
        assertThat(result).isEqualTo(sampleTask)
    }

    @Test
    fun `get should throw exception when task does not exist`() = runTest {
        // Given
        coEvery { dataSource.read() } returns emptyList()

        // When && Then
        assertThrows<TaskNotFoundException> {
            repository.get(sampleTask.id)
        }
    }

    @Test
    fun `getByProject should return tasks that match projectId`() = runTest {
        // Given
        val projectId = sampleTask.projectId

        // When
        coEvery { dataSource.read() } returns listOf(sampleTask)
        val result = repository.getByProject(projectId)

        // Then
        assertThat(result).containsExactly(sampleTask)
    }

    @Test
    fun `getByProject should return empty list when no tasks match projectId`() = runTest {
        // Given
        coEvery { dataSource.read() } returns listOf(sampleTask)

        // When
        val result = repository.getByProject(UUID.randomUUID())

        // Then
        assertThat(result).isEmpty()
    }

    private val sampleTask = Task(
        id = UUID.randomUUID(),
        projectId = UUID.randomUUID(),
        title = "Sample Task",
        description = "Description",
        stateId = UUID.randomUUID(),
        assigneeId = UUID.randomUUID(),
        creationDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    )
}