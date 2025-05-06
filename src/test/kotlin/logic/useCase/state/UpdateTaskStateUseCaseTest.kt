package logic.useCase.state

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import logic.exception.StateNotFoundException
import logic.model.TaskState
import logic.repo.TaskStateRepository
import org.damascus.logic.usecase.state.UpdateTaskStateUseCase
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
        val taskState = TaskState(UUID.randomUUID(), "In Progress")

        //given
        every { repository.exist(taskState.id) } returns true
        every { repository.update(taskState) } returns true

        //when
        val result = updateTaskStateUseCase(taskState)

        //then
        assertTrue(result)
        verify(exactly = 1) { repository.exist(taskState.id) }
        verify(exactly = 1) { repository.update(taskState) }

    }

    @Test
    fun `should throw StateNotFoundException when state doesn't exist`() {
        val taskState = TaskState(UUID.randomUUID(), "New")

        //given
        every { repository.exist(taskState.id) } returns false

        //when
         assertThrows<StateNotFoundException> {
            updateTaskStateUseCase(taskState)
        }

        //then

        verify (exactly = 1){ repository.exist(taskState.id) }
        verify (exactly = 0){ repository.update(taskState) }

    }
}