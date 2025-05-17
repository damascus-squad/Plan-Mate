package org.damascus.logic.usecase.state

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.damascus.logic.exception.StateNotFoundException
import org.damascus.logic.model.TaskState
import org.damascus.logic.repo.TaskStateRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class ManageTaskStateUseCaseTest {

    private lateinit var manageTaskStateUseCase: ManageTaskStateUseCase
    private lateinit var taskStateRepo: TaskStateRepository

    @BeforeEach
    fun setUp() {
        taskStateRepo = mockk(relaxed = true)
        manageTaskStateUseCase = ManageTaskStateUseCase(taskStateRepo)
    }

    @Test
    fun `createTaskState should call repository create method`() = runTest {
        // Given
        val taskStateName = "New State"

        // When
        manageTaskStateUseCase.createTaskState(taskStateName)

        // Then
        coVerify(exactly = 1) { taskStateRepo.create(taskStateName) }
    }

    @Test
    fun `getTaskState should call repository getTaskStateById method`() = runTest {
        // Given
        val taskId = UUID.randomUUID()

        // When
        manageTaskStateUseCase.getTaskState(taskId)

        // Then
        coVerify(exactly = 1) { taskStateRepo.getTaskStateById(taskId) }
    }

    @Test
    fun `getAllTaskStates should call repository getAllStates method`() = runTest {
        // Given
        val expectedStates = listOf(
            TaskState(UUID.randomUUID(), "State 1", 1),
            TaskState(UUID.randomUUID(), "State 2", 1)
        )

        // When
        manageTaskStateUseCase.getAllTaskStates()

        // Then
        coVerify(exactly = 1) { taskStateRepo.getAllStates() }
    }

    @Test
    fun `updateTaskState should call repository update when state exists`() = runTest {
        // Given
        val originalTaskState = TaskState(UUID.randomUUID(), "Original State", 1)
        val updatedTaskState = TaskState(originalTaskState.id, "Updated State", 1)

        coEvery { taskStateRepo.exists(updatedTaskState.name) } returns true

        // When
        manageTaskStateUseCase.updateTaskState(originalTaskState, updatedTaskState)

        // Then
        coVerify(exactly = 1) {
            taskStateRepo.exists(updatedTaskState.name)
            taskStateRepo.update(originalTaskState, updatedTaskState)
        }
    }

    @Test
    fun `updateTaskState should throw StateNotFoundException when state name exists`() = runTest {
        // Given
        val originalTaskState = TaskState(UUID.randomUUID(), "Original State", 1)
        val updatedTaskState = TaskState(originalTaskState.id, "Existing State", 1)

        coEvery { taskStateRepo.exists(updatedTaskState.name) } returns false

        // When & Then
        assertThrows<StateNotFoundException> {
            manageTaskStateUseCase.updateTaskState(originalTaskState, updatedTaskState)
        }

        coVerify(exactly = 1) { taskStateRepo.exists(updatedTaskState.name) }
        coVerify(exactly = 0) { taskStateRepo.update(any(), any()) }
    }

    @Test
    fun `deleteTaskState should call repository delete when state exists`() = runTest {
        // Given
        val taskState = TaskState(UUID.randomUUID(), "To Delete", 1)

        coEvery { taskStateRepo.exists(taskState.name) } returns true

        // When
        manageTaskStateUseCase.deleteTaskState(taskState)

        // Then
        coVerify(exactly = 1) {
            taskStateRepo.exists(taskState.name)
            taskStateRepo.delete(taskState)
        }
    }

    @Test
    fun `deleteTaskState should throw StateNotFoundException when state doesn't exist`() = runTest {
        // Given
        val taskState = TaskState(UUID.randomUUID(), "Non-existent State", 1)

        coEvery { taskStateRepo.exists(taskState.name) } returns false

        // When & Then
        assertThrows<StateNotFoundException> {
            manageTaskStateUseCase.deleteTaskState(taskState)
        }

        coVerify(exactly = 1) { taskStateRepo.exists(taskState.name) }
        coVerify(exactly = 0) { taskStateRepo.delete(any()) }
    }

}