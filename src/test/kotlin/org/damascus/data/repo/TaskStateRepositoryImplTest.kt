package org.damascus.data.repo

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.damascus.logic.exception.StateNotFoundException
import org.damascus.logic.model.History.Companion.NO_TASK_STATE
import org.damascus.logic.model.TaskState
import org.damascus.logic.repo.DataSource
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class TaskStateRepositoryImplTest {
    private lateinit var dataSource: DataSource<TaskState>
    private lateinit var stateRepositoryImpl: TaskStateRepositoryImpl

    @BeforeEach
    fun setup() {
        dataSource = mockk(relaxed = true)
        stateRepositoryImpl = TaskStateRepositoryImpl(dataSource)
    }

    @Test
    fun `getAllStates should return all states from data source`() = runTest {
        // given
        coEvery { dataSource.read() } returns fakeTaskStates

        // when
        val result = stateRepositoryImpl.getAllStates()

        //then
        assertThat(result).isEqualTo(fakeTaskStates)
        coVerify { dataSource.read() }

    }

    @Test
    fun `getStateById should return correct state when id exists`() = runTest {
        // given
        coEvery { dataSource.read() } returns fakeTaskStates

        //when
        val targetId = fakeTaskStates[0].id
        val result = stateRepositoryImpl.getTaskStateById(targetId)

        //then
        assertThat(result).isEqualTo(fakeTaskStates[0])
        coVerify { dataSource.read() }

    }

    @Test
    fun `getStateById should return NO_TASK_STATE if state doesn't exist`() = runTest {
        // given
        val nonExistentID = UUID.randomUUID()
        coEvery { dataSource.read() } returns fakeTaskStates

        // when
        val result = stateRepositoryImpl.getTaskStateById(nonExistentID)

        // Then
        assertThat(result).isEqualTo(NO_TASK_STATE)
        coVerify { dataSource.read() }
    }

    @Test
    fun `create should add new state when it doesn't exist`() = runTest {
        val newTaskState = TaskState(UUID.randomUUID(), "New", 1)

        //given
        coEvery { dataSource.read() } returns fakeTaskStates

        //when
        stateRepositoryImpl.create(newTaskState.toString())

        //then
        coVerify { dataSource.read() }

    }

    @Test
    fun `update should replace existing state`() = runTest {
        val updatedState = fakeTaskStates[1].copy(name = "New")

        //give
        coEvery { dataSource.read() } returns fakeTaskStates

        //when
        val result = stateRepositoryImpl.update(fakeTaskStates[1], updatedState)

        //then
        assertThat(result).isTrue()
        coVerify { dataSource.read() }
        coVerify { dataSource.update(fakeTaskStates[1].id, updatedState) }

    }

    @Test
    fun `update should throw StateNotFoundException when state does not exist for update`() = runTest {
        // given
        coEvery { dataSource.read() } returns fakeTaskStates

        // when
        val nonExistentTaskState = TaskState(UUID.randomUUID(), "Unknown", 1)
        val updatedTaskState = nonExistentTaskState.copy(name = "In Progress")
        //then
        assertThrows<StateNotFoundException> {
            stateRepositoryImpl.update(nonExistentTaskState, updatedTaskState)
        }
        coVerify(exactly = 1) { dataSource.read() }
        coVerify(exactly = 0) { dataSource.update(any(), any()) }
    }

    @Test
    fun `delete should remove existing state`() = runTest {
        val stateToDelete = fakeTaskStates[2]

        //given
        coEvery { dataSource.read() } returns fakeTaskStates

        // when
        val result = stateRepositoryImpl.delete(stateToDelete)

        //then
        assertThat(result).isTrue()
        coVerify(exactly = 1) { dataSource.read() }
    }

    @Test
    fun `delete should keep state & decrement projectReferencesCount by 1 when it's more than 1`() = runTest {
        // Given
        val taskStateToDelete = TaskState(
            id = UUID.randomUUID(),
            name = "SuperUsedTask",
            projectReferencesCount = 40
        )

        coEvery { dataSource.read() } returns listOf(taskStateToDelete)

        // When
        val result = stateRepositoryImpl.delete(taskStateToDelete)

        // Then
        assertThat(result).isTrue()
        coVerify(exactly = 0) { dataSource.delete(any()) }
        coVerify(exactly = 1) {
            dataSource.update(
                taskStateToDelete.id,
                taskStateToDelete.copy(
                    projectReferencesCount = 39
                )
            )
        }
    }

    @Test
    fun `delete should delete state when projectReferencesCount when it's exactly 1`() = runTest {
        // Given
        val taskStateToDelete = TaskState(
            id = UUID.randomUUID(),
            name = "SuperUsedTask",
            projectReferencesCount = 1
        )

        coEvery { dataSource.read() } returns listOf(taskStateToDelete)

        // When
        val result = stateRepositoryImpl.delete(taskStateToDelete)

        // Then
        assertThat(result).isTrue()
        coVerify(exactly = 0) { dataSource.update(any(), any()) }
        coVerify(exactly = 1) { dataSource.delete(taskStateToDelete.id) }
    }

    @Test
    fun `delete should throw StateNotFoundException when state does not exist for delete`() = runTest {
        // given
        coEvery { dataSource.read() } returns fakeTaskStates

        // when
        val taskStateToDelete = TaskState(UUID.randomUUID(), "Unknown", 1)

        // then
        assertThrows<StateNotFoundException> {
            stateRepositoryImpl.delete(taskStateToDelete)
        }
        coVerify(exactly = 1) { dataSource.read() }
        coVerify(exactly = 0) { dataSource.delete(any()) }
    }

    @Test
    fun `exist should return true when state exists`() = runTest {
        // given
        val existingState = fakeTaskStates[0]
        coEvery { dataSource.read() } returns fakeTaskStates

        // when
        val result = stateRepositoryImpl.exists(existingState.name)

        // then
        assertThat(result).isTrue()
        coVerify { dataSource.read() }
    }

    @Test
    fun `exist should return false when state does not exist`() = runTest {
        // given
        val id = UUID.randomUUID()
        val nonExistentTaskState = TaskState(id, "Done", 1)
        coEvery { dataSource.read() } returns fakeTaskStates

        // when
        val result = stateRepositoryImpl.exists(nonExistentTaskState.name)

        // then
        assertThat(result).isFalse()
        coVerify { dataSource.read() }
    }

    @Test
    fun `incrementProjectReferences should throw StateNotFoundException when state does not exist for delete`() =
        runTest {
            // Given
            val taskStateToDelete = TaskState(UUID.randomUUID(), "Unknown", 1)
            coEvery { dataSource.read() } returns fakeTaskStates

            // When && Then
            assertThrows<StateNotFoundException> {
                stateRepositoryImpl.incrementProjectReferences(taskStateToDelete)
            }

            coVerify(exactly = 1) { dataSource.read() }
            coVerify(exactly = 0) { dataSource.update(any(), any()) }
        }

    @Test
    fun `incrementProjectReferences should increment projectReferencesCount by 1 when it exists`() = runTest {
        // Given
        val taskStateToDelete = TaskState(
            id = UUID.randomUUID(),
            name = "SuperUsedTask",
            projectReferencesCount = 1
        )

        coEvery { dataSource.read() } returns listOf(taskStateToDelete)

        // When
        val result = stateRepositoryImpl.incrementProjectReferences(taskStateToDelete)

        // Then
        assertThat(result).isTrue()
        coVerify(exactly = 1) {
            dataSource.update(
                taskStateToDelete.id,
                taskStateToDelete.copy(
                    projectReferencesCount = 2
                )
            )
        }
    }

    private val fakeTaskStates = listOf(
        TaskState(UUID.fromString("00000000-0000-0000-0000-000000000001"), "In Progress", 1),
        TaskState(UUID.fromString("00000000-0000-0000-0000-000000000002"), "In Review", 1),
        TaskState(UUID.fromString("00000000-0000-0000-0000-000000000003"), "Completed", 1)
    )
}