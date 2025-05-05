package logic.useCase.state

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import logic.repo.TaskStateRepository
import org.damascus.logic.usecase.state.CheckTaskStateExistsUseCase
import org.junit.jupiter.api.BeforeEach
import java.util.*
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CheckTaskStateExistsUseCaseTest {

    private lateinit var repository: TaskStateRepository
    private lateinit var checkTaskStateExistsUseCase: CheckTaskStateExistsUseCase

    @BeforeEach
    fun setup() {
        repository = mockk(relaxed = true)
        checkTaskStateExistsUseCase = CheckTaskStateExistsUseCase(repository)
    }

    @Test
    fun `should return true when state exists`() {
        val stateId = UUID.randomUUID()

        //given
        every { repository.exist(stateId) } returns true

        //when
        val result = checkTaskStateExistsUseCase(stateId)

        //then
        assertTrue(result)
        verify(exactly = 1) { repository.exist(stateId) }
    }

    @Test
    fun `should return false when state doesn't exist`() {
        val existingId = UUID.randomUUID()

        //given
        every { repository.exist(existingId) } returns false

        //when
        val result = checkTaskStateExistsUseCase(existingId)

        //then
        assertFalse(result)
        verify(exactly = 1) { repository.exist(existingId) }
    }

}