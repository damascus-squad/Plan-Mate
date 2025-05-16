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

    private val updateUi = UpdateTaskStateUi(inputReader, taskStateRepo, manageTaskState)

    @Test
    fun `should not proceed when no states exist`() {
        every { taskStateRepo.getAllStates() } returns emptyList()

        updateUi()
        // Just ensure it does not crash — would be nice to capture output if needed
    }

    @Test
    fun `should update state successfully`() {
        val state = TaskState(UUID.randomUUID(), "OldName", 0)
        val newName = "NewName"

        every { taskStateRepo.getAllStates() } returns listOf(state)
        every { inputReader.readInt(any(), any(), any()) } returns 1
        every { inputReader.readString(any()) } returns newName

        updateUi()

        verify { manageTaskState.updateTaskState(state, state.copy(name = newName)) }
    }

    @Test
    fun `should retry input until name is not blank`() {
        val state = TaskState(UUID.randomUUID(), "Initial", 0)

        every { taskStateRepo.getAllStates() } returns listOf(state)
        every { inputReader.readInt(any(), any(), any()) } returns 1
        every { inputReader.readString(any()) } returnsMany listOf(" ", "\t", "ValidName")

        updateUi()

        verify { manageTaskState.updateTaskState(state, state.copy(name = "ValidName")) }
    }

    @Test
    fun `should handle DuplicateStateException`() {
        val state = TaskState(UUID.randomUUID(), "Old", 0)

        every { taskStateRepo.getAllStates() } returns listOf(state)
        every { inputReader.readInt(any(), any(), any()) } returns 1
        every { inputReader.readString(any()) } returns "DupName"
        every { manageTaskState.updateTaskState(any(), any()) } throws DuplicateStateException("Exists")

        updateUi()

        verify { manageTaskState.updateTaskState(state, state.copy(name = "DupName")) }
    }

    @Test
    fun `should handle StateNotFoundException`() {
        val state = TaskState(UUID.randomUUID(), "StateX", 0)

        every { taskStateRepo.getAllStates() } returns listOf(state)
        every { inputReader.readInt(any(), any(), any()) } returns 1
        every { inputReader.readString(any()) } returns "Updated"
        every { manageTaskState.updateTaskState(any(), any()) } throws StateNotFoundException()

        updateUi()

        verify { manageTaskState.updateTaskState(state, state.copy(name = "Updated")) }
    }
}