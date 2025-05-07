package logic.usecase.state

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import logic.exception.StateNotFoundException
import logic.model.TaskState
import logic.repo.TaskStateRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import java.util.*
import kotlin.test.Test
import kotlin.test.assertTrue

class DeleteTaskStateUseCaseTest {

    private lateinit var repository: TaskStateRepository
    private lateinit var deleteTaskStateUseCase: DeleteTaskStateUseCase

    @BeforeEach
    fun setup() {
        repository = mockk(relaxed = true)
        deleteTaskStateUseCase = DeleteTaskStateUseCase(repository)
    }

    @Test
    fun `should delete state when it exists`() {
        val taskState = TaskState(UUID.randomUUID(), "In Progress", 1)

        //given
        every { repository.exist(taskState.name) } returns true
        every { repository.delete(taskState) } returns true

        //when
        val result = deleteTaskStateUseCase(taskState)

        //then
        assertTrue(result)

        verify(exactly = 1) { repository.exist(taskState.name) }
        verify(exactly = 1) { repository.delete(taskState) }

    }

    @Test
    fun `should throw StateNotFoundException when state doesn't exist `() {
        val taskState = TaskState(UUID.randomUUID(), "New", 1)

        //given
        every { repository.exist(taskState.name) } returns false

        //when && then
        assertThrows<StateNotFoundException> {
            deleteTaskStateUseCase(taskState)
        }

        verify(exactly = 1) { repository.exist(taskState.name) }
        verify(exactly = 0) { repository.delete(taskState) }

    }


}