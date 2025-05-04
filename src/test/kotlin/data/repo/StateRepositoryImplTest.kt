package data.repo

import com.google.common.truth.Truth.assertThat
import io.mockk.*
import logic.exception.DuplicateStateException
import logic.exception.StateNotFoundException
import logic.model.TaskState
import logic.repo.DataSource
import org.damascus.data.repo.TaskStateRepositoryImpl
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
    fun `should return all states from data source`() {
        // given
        every { dataSource.read() } returns fakeTaskStates

        // when
        val result = stateRepositoryImpl.getAllStates()

        //then
        assertThat(result).isEqualTo(fakeTaskStates)
        verify(exactly = 1) { dataSource.read() }

    }

    @Test
    fun `should return correct state when id exists`() {
        // given
        every { dataSource.read() } returns fakeTaskStates

        //when
        val targetId = fakeTaskStates[0].id
        val result = stateRepositoryImpl.getStateById(targetId)

        //then
        assertThat(result).isEqualTo(fakeTaskStates[0])
        verify(exactly = 1) { dataSource.read() }

    }

    @Test
    fun `should return null if it doesn't exist`() {
        // given
        every { dataSource.read() } returns fakeTaskStates

        // when
        val nonExistentID = UUID.randomUUID()
        val result = stateRepositoryImpl.getStateById(nonExistentID)

        //then
        assertThat(result).isNull()
        verify(exactly = 1) { dataSource.read() }
    }

    @Test
    fun `should create new state when it doesn't exist`() {
        val newTaskState = TaskState(UUID.randomUUID(), "New")

        //given
        every { dataSource.read() } returns fakeTaskStates
        every { dataSource.write(newTaskState) } just runs

        //when
        val result = stateRepositoryImpl.create(newTaskState)

        //then
        assertThat(result).isTrue()
        verify(exactly = 1) { dataSource.read() }
        verify(exactly = 1) { dataSource.write(newTaskState) }

    }

    @Test
    fun `should throw DuplicateStateException when state already exists`() {
        val existingState = fakeTaskStates[0]

        // given
        every { dataSource.read() } returns fakeTaskStates

        // when && then
        val exception = assertThrows<DuplicateStateException> {
            stateRepositoryImpl.create(existingState)
        }
        verify(exactly = 1) { dataSource.read() }
        verify(exactly = 0) { dataSource.write(any<TaskState>()) }
    }

    @Test
    fun `should update existing state`() {
        val updatedState = fakeTaskStates[1].copy(name = "Deleted")

        //give
        every { dataSource.read() } returns fakeTaskStates
        every { dataSource.update(updatedState.id, updatedState) } just runs

        //when
        val result = stateRepositoryImpl.update(updatedState)

        //then
        assertThat(result).isTrue()
        verify(exactly = 1) { dataSource.read() }
        verify(exactly = 1) { dataSource.update(updatedState.id, updatedState) }

    }

    @Test
    fun `should throw StateNotFoundException when state does not exist for update`() {
        // given
        every { dataSource.read() } returns fakeTaskStates

        // when
        val nonExistentTaskState = TaskState(UUID.randomUUID(), "Unknown")

        //then
        val exception = assertThrows<StateNotFoundException> {
            stateRepositoryImpl.update(nonExistentTaskState)
        }
        verify(exactly = 1) { dataSource.read() }
        verify(exactly = 0) { dataSource.update(any(), any()) }
    }

    @Test
    fun `should delete existing state`() {
        val stateToDelete = fakeTaskStates[2]

        //given
        every { dataSource.read() } returns fakeTaskStates
        every { dataSource.delete(stateToDelete.id) } just runs

        // when
        val result = stateRepositoryImpl.delete(stateToDelete)

        //then
        assertThat(result).isTrue()
        verify(exactly = 1) { dataSource.read() }
        verify (exactly = 1){ dataSource.delete(stateToDelete.id) }
    }

    @Test
    fun `should throw StateNotFoundException when state does not exist for delete`() {
        // given
        every { dataSource.read() } returns fakeTaskStates

        // when
        val taskStateToDelete = TaskState(UUID.randomUUID(), "Unknown")

        // then
        val exception = assertThrows<StateNotFoundException> {
            stateRepositoryImpl.delete(taskStateToDelete)
        }
        verify(exactly = 1) { dataSource.read() }
        verify(exactly = 0) { dataSource.delete(any()) }
    }

    @Test
    fun `should return true when state exists`() {
        // given
        val existingState = fakeTaskStates[0]
        every { dataSource.read() } returns fakeTaskStates

        // when
        val result = stateRepositoryImpl.exist(existingState.id)

        // then
        assertThat(result).isTrue()
        verify(exactly = 1) { dataSource.read() }
    }

    @Test
    fun `should return false when state does not exist`() {
        // given
        val nonExistentId = UUID.randomUUID()
        every { dataSource.read() } returns fakeTaskStates

        // when
        val result = stateRepositoryImpl.exist(nonExistentId)

        // then
        assertThat(result).isFalse()
        verify(exactly = 1) { dataSource.read() }
    }

    private val fakeTaskStates = listOf(
        TaskState(UUID.fromString("00000000-0000-0000-0000-000000000001"), "In Progress"),
        TaskState(UUID.fromString("00000000-0000-0000-0000-000000000002"), "In Review"),
        TaskState(UUID.fromString("00000000-0000-0000-0000-000000000003"), "Completed")

    )
}