package org.damascus.ui.views.taskState

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
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
    fun `should delete selected state`() {
        val state = mockk<TaskState> {
            every { name } returns "Test"
            every { projectReferencesCount } returns 1
        }

        every { taskStateRepo.getAllStates() } returns listOf(state)
        every { inputReader.readInt(any(), any(), any()) } returns 1
        every { inputReader.readString(any()) } returns "yes"
        every { taskStateRepo.delete(state) } returns true

        deleteTaskStateUi()

        verify { taskStateRepo.delete(state) }
    }

    @Test
    fun `should cancel deletion if not confirmed`() {
        val state = mockk<TaskState> {
            every { name } returns "Test"
            every { projectReferencesCount } returns 1
        }

        every { taskStateRepo.getAllStates() } returns listOf(state)
        every { inputReader.readInt(any(), any(), any()) } returns 1
        every { inputReader.readString(any()) } returns "no"

        deleteTaskStateUi()

        verify(exactly = 0) { taskStateRepo.delete(any()) }
    }
    @Test
    fun `should print warning when no states are available`() {
        every { taskStateRepo.getAllStates() } returns emptyList()

        deleteTaskStateUi()

        verify(exactly = 0) { taskStateRepo.delete(any()) }
    }

    @Test
    fun `should decrement reference count when reference more than 1`() {
        val state = TaskState(UUID.randomUUID(), "Test", 2)

        every { taskStateRepo.getAllStates() } returns listOf(state)
        every { inputReader.readInt(any(), any(), any()) } returns 1
        every { inputReader.readString(any()) } returns "yes"
        every { taskStateRepo.delete(state) } returns true

        deleteTaskStateUi()

        verify { taskStateRepo.delete(state) }
    }

    @Test
    fun `should handle exception during deletion`() {
        val state = TaskState(UUID.randomUUID(), "ErrorState", 1)

        every { taskStateRepo.getAllStates() } returns listOf(state)
        every { inputReader.readInt(any(), any(), any()) } returns 1
        every { inputReader.readString(any()) } returns "yes"
        every { taskStateRepo.delete(state) } throws RuntimeException("Deletion failed")

        deleteTaskStateUi()

        verify { taskStateRepo.delete(state) }
    }


}
