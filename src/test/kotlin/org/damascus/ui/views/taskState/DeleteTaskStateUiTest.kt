package org.damascus.ui.views.taskState

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.damascus.logic.model.TaskState
import org.damascus.logic.repo.TaskStateRepository
import org.damascus.ui.io.InputReader
import java.util.*
import kotlin.test.Test

class DeleteTaskStateUiTest {
    private val inputReader = mockk<InputReader>()
    private val taskStateRepo = mockk<TaskStateRepository>()
    private val deleteTaskStateUi = DeleteTaskStateUi(inputReader, taskStateRepo)

    @Test
    fun `should delete selected state`() = runTest {
        val state = mockk<TaskState> {
            coEvery { name } returns "Test"
            coEvery { projectReferencesCount } returns 1
        }

        coEvery { taskStateRepo.getAllStates() } returns listOf(state)
        coEvery { inputReader.readInt(any(), any(), any()) } returns 1
        coEvery { inputReader.readString(any()) } returns "yes"
        coEvery { taskStateRepo.delete(state) } returns true

        deleteTaskStateUi()

        coVerify { taskStateRepo.delete(state) }
    }

    @Test
    fun `should cancel deletion if not confirmed`() = runTest {
        val state = mockk<TaskState> {
            coEvery { name } returns "Test"
            coEvery { projectReferencesCount } returns 1
        }

        coEvery { taskStateRepo.getAllStates() } returns listOf(state)
        coEvery { inputReader.readInt(any(), any(), any()) } returns 1
        coEvery { inputReader.readString(any()) } returns "no"

        deleteTaskStateUi()

        coVerify(exactly = 0) { taskStateRepo.delete(any()) }
    }

    @Test
    fun `should print warning when no states are available`() = runTest {
        coEvery { taskStateRepo.getAllStates() } returns emptyList()

        deleteTaskStateUi()

        coVerify(exactly = 0) { taskStateRepo.delete(any()) }
    }

    @Test
    fun `should decrement reference count when reference more than 1`() = runTest {
        val state = TaskState(UUID.randomUUID(), "Test", 2)

        coEvery { taskStateRepo.getAllStates() } returns listOf(state)
        coEvery { inputReader.readInt(any(), any(), any()) } returns 1
        coEvery { inputReader.readString(any()) } returns "yes"
        coEvery { taskStateRepo.delete(state) } returns true

        deleteTaskStateUi()

        coVerify { taskStateRepo.delete(state) }
    }

    @Test
    fun `should handle exception during deletion`() = runTest {
        val state = TaskState(UUID.randomUUID(), "ErrorState", 1)

        coEvery { taskStateRepo.getAllStates() } returns listOf(state)
        coEvery { inputReader.readInt(any(), any(), any()) } returns 1
        coEvery { inputReader.readString(any()) } returns "yes"
        coEvery { taskStateRepo.delete(state) } throws RuntimeException("Deletion failed")

        deleteTaskStateUi()

        coVerify { taskStateRepo.delete(state) }
    }


}
