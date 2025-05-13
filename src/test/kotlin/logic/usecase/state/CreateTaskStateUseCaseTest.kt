package logic.usecase.state

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import logic.model.TaskState
import logic.repo.TaskStateRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
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
        val newStateName = "ToDo"
        val fakeTaskState = TaskState(id = UUID.randomUUID(), newStateName, 1)

        //given
        every { repository.exist(newStateName) } returns false
        every { repository.create(newStateName) } returns fakeTaskState

        //when
        val result = createTaskStateUseCase(fakeTaskState)

        //then
        assertThat(result).isEqualTo(fakeTaskState)
        verify(exactly = 1) { repository.create(newStateName) }
    }

}
