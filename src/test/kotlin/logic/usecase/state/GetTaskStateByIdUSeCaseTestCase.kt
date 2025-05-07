package logic.usecase.state

import com.google.common.truth.Truth.assertThat
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

class GetTaskStateByIdUSeCaseTestCase {

    private lateinit var repository: TaskStateRepository
    private lateinit var getTaskStateByIdUseCase: GetTaskStateByIdUseCase

    @BeforeEach
    fun setup() {
        repository = mockk(relaxed = true)
        getTaskStateByIdUseCase = GetTaskStateByIdUseCase(repository)
    }

    @Test
    fun `should return task state when it exists`() {
        val taskState = TaskState(UUID.randomUUID(), "In Progress", 1)

        //given
        every { repository.getTaskStateById(taskState.id) } returns taskState

        //when
        val result = getTaskStateByIdUseCase(taskState.id)

        //then
        assertThat(result).isEqualTo(taskState)
        verify(exactly = 1) { repository.getTaskStateById(taskState.id) }
    }


    @Test
    fun `should throw StateNotFoundException when task state is not found`() {
        val taskStateId = UUID.randomUUID()

        // given
        every { repository.getTaskStateById(taskStateId) } returns null

        // when && then
        assertThrows<StateNotFoundException> {
            getTaskStateByIdUseCase(taskStateId)
        }

        verify(exactly = 1) { repository.getTaskStateById(taskStateId) }
    }


}