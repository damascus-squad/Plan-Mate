package data.repo

import com.google.common.truth.Truth.assertThat
import io.mockk.*
import logic.exception.DuplicateStateException
import logic.exception.StateNotFoundException
import logic.model.State
import logic.repo.DataSource
import org.damascus.data.repo.TaskStateRepositoryImpl
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class StateRepositoryImplTest {
    private lateinit var dataSource: DataSource<State>
    private lateinit var stateRepositoryImpl: TaskStateRepositoryImpl

    @BeforeEach
    fun setup() {
        dataSource = mockk(relaxed = true)
        stateRepositoryImpl = TaskStateRepositoryImpl(dataSource)
    }

    @Test
    fun `should return all states from data source`() {
        // given
        every { dataSource.read() } returns fakeStates

        // when
        val result = stateRepositoryImpl.getAllStates()

        //then
        assertThat(result).isEqualTo(fakeStates)
        verify(exactly = 1) { dataSource.read() }

    }

    @Test
    fun `should return correct state when id exists`() {
        // given
        every { dataSource.read() } returns fakeStates

        //when
        val targetId = fakeStates[0].id
        val result = stateRepositoryImpl.getStateById(targetId)

        //then
        assertThat(result).isEqualTo(fakeStates[0])
        verify(exactly = 1) { dataSource.read() }

    }

    @Test
    fun `should return null if it doesn't exist`() {
        // given
        every { dataSource.read() } returns fakeStates

        // when
        val nonExistentID = UUID.randomUUID()
        val result = stateRepositoryImpl.getStateById(nonExistentID)

        //then
        assertThat(result).isNull()
        verify(exactly = 1) { dataSource.read() }
    }

    @Test
    fun `should create new state when it doesn't exist`() {
        val newState = State(UUID.randomUUID(), "New")

        //given
        every { dataSource.read() } returns fakeStates
        every { dataSource.write(newState) } just runs

        //when
        val result = stateRepositoryImpl.create(newState)

        //then
        assertThat(result).isTrue()
        verify(exactly = 1) { dataSource.read() }
        verify(exactly = 1) { dataSource.write(newState) }

    }

    @Test
    fun `should throw DuplicateStateException when state already exists`() {
        val existingState = fakeStates[0]

        // given
        every { dataSource.read() } returns fakeStates

        // when && then
        val exception = assertThrows<DuplicateStateException> {
            stateRepositoryImpl.create(existingState)
        }
        verify(exactly = 1) { dataSource.read() }
        verify(exactly = 0) { dataSource.write(any<State>()) }
    }

    @Test
    fun `should update existing state`() {
        val updatedState = fakeStates[1].copy(name = "Deleted")

        //give
        every { dataSource.read() } returns fakeStates
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
        every { dataSource.read() } returns fakeStates

        // when
        val nonExistentState = State(UUID.randomUUID(), "Unknown")

        //then
        val exception = assertThrows<StateNotFoundException> {
            stateRepositoryImpl.update(nonExistentState)
        }
        verify(exactly = 1) { dataSource.read() }
        verify(exactly = 0) { dataSource.update(any(), any()) }
    }

    @Test
    fun `should delete existing state`() {
        val stateToDelete = fakeStates[2]

        //given
        every { dataSource.read() } returns fakeStates
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
        every { dataSource.read() } returns fakeStates

        // when
        val stateToDelete = State(UUID.randomUUID(), "Unknown")

        // then
        val exception = assertThrows<StateNotFoundException> {
            stateRepositoryImpl.delete(stateToDelete)
        }
        verify(exactly = 1) { dataSource.read() }
        verify(exactly = 0) { dataSource.delete(any()) }
    }

    @Test
    fun `should return true when state exists`() {
        // given
        val existingState = fakeStates[0]
        every { dataSource.read() } returns fakeStates

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
        every { dataSource.read() } returns fakeStates

        // when
        val result = stateRepositoryImpl.exist(nonExistentId)

        // then
        assertThat(result).isFalse()
        verify(exactly = 1) { dataSource.read() }
    }

    private val fakeStates = listOf(
        State(UUID.fromString("00000000-0000-0000-0000-000000000001"), "In Progress"),
        State(UUID.fromString("00000000-0000-0000-0000-000000000002"), "In Review"),
        State(UUID.fromString("00000000-0000-0000-0000-000000000003"), "Completed")

    )
}