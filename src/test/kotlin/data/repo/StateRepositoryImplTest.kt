package data.repo

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import logic.exception.DuplicateStateException
import logic.exception.StateNotFoundException
import logic.model.TaskState
import logic.repo.DataSource
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class StateRepositoryImplTest {
    private lateinit var dataSource: DataSource<TaskState>
    private lateinit var stateRepositoryImpl: TaskStateRepositoryImpl

    @BeforeEach
    fun setup() {
        dataSource = mockk(relaxed = true)
        stateRepositoryImpl = TaskStateRepositoryImpl(dataSource)
    }

    @Test
    fun `getAllStates should return all states from data source`() {
        // given
        every { dataSource.read() } returns fakeTaskStates

        // when
        val result = stateRepositoryImpl.getAllStates()

        //then
        assertThat(result).isEqualTo(fakeTaskStates)
        verify { dataSource.read() }

    }

    @Test
    fun `getStateById should return correct state when id exists`() {
        // given
        every { dataSource.read() } returns fakeTaskStates

        //when
        val targetId = fakeTaskStates[0].id
        val result = stateRepositoryImpl.getTaskStateById(targetId)

        //then
        assertThat(result).isEqualTo(fakeTaskStates[0])
        verify { dataSource.read() }

    }

    @Test
    fun `getStateById should throw StateNotFoundException if state doesn't exist`() {
        // given
        val nonExistentID = UUID.randomUUID()
        every { dataSource.read() } returns fakeTaskStates

        // when && then
        assertThrows<StateNotFoundException> {
            stateRepositoryImpl.getTaskStateById(nonExistentID)
        }
        verify { dataSource.read() }
    }

    @Test
    fun `create should add new state when it doesn't exist`() {
        val newTaskState = TaskState(UUID.randomUUID(), "New", 1)

        //given
        every { dataSource.read() } returns fakeTaskStates

        //when
        val result = stateRepositoryImpl.create(newTaskState)

        //then
        assertThat(result).isTrue()
        verify { dataSource.read() }

    }

    @Test
    fun `create should throw DuplicateStateException when state already exists`() {
        val existingState = fakeTaskStates[0]

        // given
        every { dataSource.read() } returns fakeTaskStates

        // when && then
        assertThrows<DuplicateStateException> {
            stateRepositoryImpl.create(existingState)
        }
        verify { dataSource.read() }
    }

    @Test
    fun `update should replace existing state`() {
        val updatedState = fakeTaskStates[1].copy(name = "New")

        //give
        every { dataSource.read() } returns fakeTaskStates

        //when
        val result = stateRepositoryImpl.update(fakeTaskStates[1], updatedState)

        //then
        assertThat(result).isTrue()
        verify { dataSource.read() }
        verify { dataSource.update(fakeTaskStates[1].id, updatedState) }

    }

    @Test
    fun `update should throw StateNotFoundException when state does not exist for update`() {
        // given
        every { dataSource.read() } returns fakeTaskStates

        // when
        val nonExistentTaskState = TaskState(UUID.randomUUID(), "Unknown", 1)
        val updatedTaskState = nonExistentTaskState.copy(name = "In Progress")
        //then
        assertThrows<StateNotFoundException> {
            stateRepositoryImpl.update(nonExistentTaskState, updatedTaskState)
        }
        verify(exactly = 1) { dataSource.read() }
        verify(exactly = 0) { dataSource.update(any(), any()) }
    }

    @Test
    fun `delete should remove existing state`() {
        val stateToDelete = fakeTaskStates[2]

        //given
        every { dataSource.read() } returns fakeTaskStates

        // when
        val result = stateRepositoryImpl.delete(stateToDelete)

        //then
        assertThat(result).isTrue()
        verify(exactly = 1) { dataSource.read() }
    }

    @Test
    fun `delete should throw StateNotFoundException when state does not exist for delete`() {
        // given
        every { dataSource.read() } returns fakeTaskStates

        // when
        val taskStateToDelete = TaskState(UUID.randomUUID(), "Unknown", 1)

        // then
        assertThrows<StateNotFoundException> {
            stateRepositoryImpl.delete(taskStateToDelete)
        }
        verify(exactly = 1) { dataSource.read() }
        verify(exactly = 0) { dataSource.delete(any()) }
    }

    @Test
    fun `exist should return true when state exists`() {
        // given
        val existingState = fakeTaskStates[0]
        every { dataSource.read() } returns fakeTaskStates

        // when
        val result = stateRepositoryImpl.exist(existingState.name)

        // then
        assertThat(result).isTrue()
        verify { dataSource.read() }
    }

    @Test
    fun `exist should return false when state does not exist`() {
        // given
        val id = UUID.randomUUID()
        val nonExistentTaskState = TaskState(id, "Done", 1)
        every { dataSource.read() } returns fakeTaskStates

        // when
        val result = stateRepositoryImpl.exist(nonExistentTaskState.name)

        // then
        assertThat(result).isFalse()
        verify { dataSource.read() }
    }

    private val fakeTaskStates = listOf(
        TaskState(UUID.fromString("00000000-0000-0000-0000-000000000001"), "In Progress", 1),
        TaskState(UUID.fromString("00000000-0000-0000-0000-000000000002"), "In Review", 1),
        TaskState(UUID.fromString("00000000-0000-0000-0000-000000000003"), "Completed", 1)

    )
}