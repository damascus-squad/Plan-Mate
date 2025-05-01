package data.repo

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import model.State
import org.damascus.data.repo.StateRepositoryImpl
import org.damascus.data.source.StateDataSource
import org.damascus.logic.exception.DuplicateStateException
import org.damascus.logic.exception.StateNotFoundException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class StateRepositoryImplTest {
    private lateinit var stateDataSource: StateDataSource<State>
    private lateinit var stateRepositoryImpl: StateRepositoryImpl

    @BeforeEach
    fun setup() {
        stateDataSource = mockk(relaxed = true)
        stateRepositoryImpl = StateRepositoryImpl(stateDataSource)
    }

    @Test
    fun `should return all states from data source`() {
        // given
        every { stateDataSource.read() } returns fakeStates

        // when
        val result = stateRepositoryImpl.getAllStates()

        //then
        assertThat(result).isEqualTo(fakeStates)

    }

    @Test
    fun `should return correct state when id exists`() {
        // given
        every { stateDataSource.read() } returns fakeStates

        //when
        val targetId = fakeStates[0].id
        val result = stateRepositoryImpl.getStateById(targetId)

        //then
        assertThat(result).isEqualTo(fakeStates[0])
    }

    @Test
    fun `should return null if it doesn't exist`() {
        // given
        every { stateDataSource.read() } returns fakeStates

        // when
        val nonExistentID = UUID.randomUUID()
        val result = stateRepositoryImpl.getStateById(nonExistentID)

        //then
        assertThat(result).isNull()
    }

    @Test
    fun `should create new state when it doesn't exist`() {
        //given
        every { stateDataSource.read() } returns fakeStates
        every { stateDataSource.write(any()) } returns true

        //when
        val newState = State(UUID.randomUUID(), "New")
        val result = stateRepositoryImpl.create(newState)

        //then
        assertThat(result).isTrue()

        verify { stateDataSource.write(newState) }
    }

    @Test
    fun `should throw DuplicateStateException when state already exists`() {
        // given
        every { stateDataSource.read() } returns fakeStates
        // when
        val existingState = fakeStates[0]
        // then
        val exception = assertThrows<DuplicateStateException> {
            stateRepositoryImpl.create(existingState)
        }

        verify(exactly = 0) { stateDataSource.write(any()) }
    }

    @Test
    fun `should update existing state`() {
        //give
        every { stateDataSource.read() } returns fakeStates
        every { stateDataSource.update(any(), any()) } returns true

        //when
        val updatedState = fakeStates[1].copy(name = "Deleted")
        val result = stateRepositoryImpl.update(updatedState)

        //then
        assertThat(result).isTrue()

        verify { stateDataSource.update(updatedState.id, updatedState) }

    }

    @Test
    fun `should throw StateNotFoundException when state does not exist for update`() {
        // given
        every { stateDataSource.read() } returns fakeStates

        // when
        val nonExistentState = State(UUID.randomUUID(), "Unknown")

        //then
        val exception = assertThrows<StateNotFoundException> {
            stateRepositoryImpl.update(nonExistentState)
        }

        verify(exactly = 0) { stateDataSource.update(any(), any()) }
    }

    @Test
    fun `should delete existing state`() {
        val stateToDelete = fakeStates[2]

        //given
        every { stateDataSource.read() } returns fakeStates
        every { stateDataSource.delete(stateToDelete.id) } returns true

        // when
        val result = stateRepositoryImpl.delete(stateToDelete)

        //then
        assertThat(result).isTrue()

        verify { stateDataSource.delete(stateToDelete.id) }
    }

    @Test
    fun `should throw StateNotFoundException when state does not exist for delete`() {
        // given
        every { stateDataSource.read() } returns fakeStates

        // when
        val stateToDelete = State(UUID.randomUUID(), "Unknown")

        // then
        val exception = assertThrows<StateNotFoundException> {
            stateRepositoryImpl.delete(stateToDelete)
        }

        verify(exactly = 0) { stateDataSource.delete(any()) }
    }


    private val fakeStates = listOf(
        State(UUID.fromString("00000000-0000-0000-0000-000000000001"), "In Progress"),
        State(UUID.fromString("00000000-0000-0000-0000-000000000002"), "In Review"),
        State(UUID.fromString("00000000-0000-0000-0000-000000000003"), "Completed")

    )
}