package logic.usecase.state

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import logic.model.TaskState
import logic.repo.TaskStateRepository
import org.junit.jupiter.api.BeforeEach
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetAllTaskStatesUseCaseTest {

    private lateinit var repository: TaskStateRepository
    private lateinit var getAllTaskStatesUseCase: GetAllTaskStatesUseCase

    @BeforeEach
    fun setup() {
        repository = mockk(relaxed = true)
        getAllTaskStatesUseCase = GetAllTaskStatesUseCase(repository)
    }

    @Test
    fun `should return all task states `() {
        val taskStates = listOf(
            TaskState(UUID.randomUUID(), "In Progress"),
            TaskState(UUID.randomUUID(), "Done")
        )

        // given
        every { repository.getAllStates() } returns taskStates

        //when
        val result = getAllTaskStatesUseCase()

        //then
        assertEquals(result, taskStates)

        verify(exactly = 1) { repository.getAllStates() }
    }

    @Test
    fun `should return empty list when no task states exist`() {
        //given
        every { repository.getAllStates() } returns emptyList()

        //when
        val result = getAllTaskStatesUseCase()

        //then
        assertTrue(result.isEmpty())
        verify(exactly = 1) { repository.getAllStates() }
    }
}