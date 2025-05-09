package logic.usecase.state

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import logic.model.TaskState
import logic.repo.TaskStateRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class IncrementTaskStateReferencesUseCaseTest {

    private lateinit var taskStateRepository: TaskStateRepository
    private lateinit var incrementTaskStateReferencesUseCase: IncrementTaskStateReferencesUseCase

    @BeforeEach
    fun setup() {
        taskStateRepository = mockk(relaxed = true)
        incrementTaskStateReferencesUseCase = IncrementTaskStateReferencesUseCase(taskStateRepository)
    }

    @Test
    fun `should return true when state exists`() {
        // Given
        val taskState = TaskState( UUID.randomUUID(), "In Progress", 1)
        every { taskStateRepository.exist(taskState.name) } returns true

        // When
        incrementTaskStateReferencesUseCase(taskState)

        // Then
        verify(exactly = 1) { taskStateRepository.incrementProjectReferences(taskState) }
    }

}