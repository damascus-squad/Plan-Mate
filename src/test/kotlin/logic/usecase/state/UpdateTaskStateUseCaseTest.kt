package logic.usecase.state

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import logic.exception.StateNotFoundException
import logic.model.TaskState
import logic.repo.TaskStateRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*
import kotlin.test.assertTrue

class UpdateTaskStateUseCaseTest {

    private lateinit var repository: TaskStateRepository
    private lateinit var updateTaskStateUseCase: UpdateTaskStateUseCase

    @BeforeEach
    fun setup() {
        repository = mockk(relaxed = true)
        updateTaskStateUseCase = UpdateTaskStateUseCase(repository)
    }

    @Test
    fun `should update state when it exists`() {
        val updatedTaskState = TaskState(UUID.randomUUID(), "In Progress", 1)
        val existTaskState = TaskState(UUID.randomUUID(), "Done", 1)
        //given
        every { repository.exist(updatedTaskState.name) } returns true
        every { repository.update(existTaskState, updatedTaskState) } returns true

        //when
        val result = updateTaskStateUseCase(existTaskState, updatedTaskState)

        //then
        assertTrue(result)
        verify(exactly = 1) { repository.exist(updatedTaskState.name) }
        verify(exactly = 1) { repository.update(existTaskState, updatedTaskState) }

    }

    @Test
    fun `should throw StateNotFoundException when state doesn't exist`() {
        val updatedTaskState = TaskState(UUID.randomUUID(), "New", 1)
        val existTaskState = TaskState(UUID.randomUUID(), "Done", 1)

        //given
        every { repository.exist(updatedTaskState.name) } returns false

        //when
        assertThrows<StateNotFoundException> {
            updateTaskStateUseCase(existTaskState, updatedTaskState)
        }

        //then
        verify(exactly = 1) { repository.exist(updatedTaskState.name) }
        verify(exactly = 0) { repository.update(existTaskState, updatedTaskState) }

    }
}