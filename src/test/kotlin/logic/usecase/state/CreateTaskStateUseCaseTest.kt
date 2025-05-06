package logic.usecase.state

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import logic.exception.DuplicateStateException
import logic.model.TaskState
import logic.repo.TaskStateRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class CreateTaskStateUseCaseTest {

    private lateinit var repository: TaskStateRepository
    private lateinit var createTaskStateUseCase: CreateTaskStateUseCase

    @BeforeEach
    fun setup() {
        repository = mockk(relaxed = true)
        createTaskStateUseCase = CreateTaskStateUseCase(repository)
    }

    @Test
    fun `should create new task state when it does not exist`() {
        val newState = TaskState(UUID.randomUUID(), "To Do")

        //given
        every { repository.exist(newState.name) } returns false
        every { repository.create(newState) } returns true

        //when
        val result = createTaskStateUseCase(newState)

        //then
        assertThat(result).isTrue()
        verify(exactly = 1) { repository.exist(newState.name) }
        verify(exactly = 1) { repository.create(newState) }
    }

    @Test
    fun `should throw DuplicateStateException when task state already exists`() {
        val existingId = UUID.randomUUID()
        val existingState = TaskState(existingId, "In Progress")

        // given
        every { repository.exist(existingState.name) } returns true

        // when && then
        assertThrows<DuplicateStateException> {
            createTaskStateUseCase(existingState)
        }

        verify(exactly = 1) { repository.exist(existingState.name) }
        verify(exactly = 0) { repository.create(existingState) }
    }

}
