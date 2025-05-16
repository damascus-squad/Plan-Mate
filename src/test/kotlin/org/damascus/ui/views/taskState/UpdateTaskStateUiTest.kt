package org.damascus.ui.views.taskState

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.damascus.logic.exception.DuplicateStateException
import org.damascus.logic.exception.StateNotFoundException
import org.damascus.logic.model.TaskState
import org.damascus.logic.repo.TaskStateRepository
import org.damascus.logic.usecase.state.ManageTaskStateUseCase
import org.damascus.ui.io.InputReader
import java.util.*
import kotlin.test.Test

class UpdateTaskStateUiTest {

    private val inputReader = mockk<InputReader>()
    private val taskStateRepo = mockk<TaskStateRepository>()
    private val manageTaskState = mockk<ManageTaskStateUseCase>(relaxed = true)

    private val updateTaskStateUi = UpdateTaskStateUi(inputReader, taskStateRepo, manageTaskState)

    @Test
    fun `should update state with new name`() {
        val oldState = TaskState(UUID.randomUUID(), "Old", 0)
        val newName = "New"

        every { taskStateRepo.getAllStates() } returns listOf(oldState)
        every { inputReader.readInt(any(), any(), any()) } returns 1
        every { inputReader.readString(any()) } returns newName

        updateTaskStateUi()

        verify { manageTaskState.updateTaskState(oldState, oldState.copy(name = newName)) }
    }

    @Test
    fun `should skip update when name is blank`() {
        val state = TaskState(UUID.randomUUID(), "Current", 0)

        every { taskStateRepo.getAllStates() } returns listOf(state)
        every { inputReader.readInt(any(), any(), any()) } returns 1
        every { inputReader.readString(any()) } returns ""

        updateTaskStateUi()

        verify { manageTaskState.updateTaskState(state, state.copy(name = "Current")) }
    }

    @Test
    fun `should show warning when no states exist`() {
        every { taskStateRepo.getAllStates() } returns emptyList()

        updateTaskStateUi()
    }
    @Test
    fun `should handle when state is duplicated `() {
        val state = TaskState(UUID.randomUUID(), "Old", 0)
        every { taskStateRepo.getAllStates() } returns listOf(state)
        every { inputReader.readInt(any(), any(), any()) } returns 1
        every { inputReader.readString(any()) } returns "Duplicate"
        every {
            manageTaskState.updateTaskState(any(), any())
        } throws DuplicateStateException("Exists")

        updateTaskStateUi()
    }
    @Test
    fun `should handle when state not found`() {
        val state = TaskState(UUID.randomUUID(), "ToUpdate", 0)
        every { taskStateRepo.getAllStates() } returns listOf(state)
        every { inputReader.readInt(any(), any(), any()) } returns 1
        every { inputReader.readString(any()) } returns "Updated"
        every {
            manageTaskState.updateTaskState(any(), any())
        } throws StateNotFoundException()

        updateTaskStateUi()
    }

}
