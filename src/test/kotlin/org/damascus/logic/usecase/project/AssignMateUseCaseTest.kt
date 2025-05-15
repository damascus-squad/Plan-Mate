package org.damascus.logic.usecase.project

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDateTime
import org.damascus.logic.model.Project
import org.damascus.logic.repo.ProjectRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class AssignMateUseCaseTest {

    private lateinit var repository: ProjectRepository
    private lateinit var useCase: ManageMateAssignmentUseCase

    @BeforeEach
    fun setup() {
        repository = mockk(relaxed = true)
        useCase = ManageMateAssignmentUseCase(repository)
    }

    @Test
    fun `should return false when assigning mate to non-existing project`() = runTest {
        // Given
        coEvery { repository.get(any()) } throws Exception()

        // When
        val result = useCase.assign(UUID.randomUUID(), UUID.randomUUID())

        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun `should return false when assigning mate already assigned`() = runTest {
        // Given
        val mateId = UUID.randomUUID()
        val project = makeProject().apply { assignedMatesIds.add(mateId) }
        coEvery { repository.get(project.id) } returns project

        // When
        val result = useCase.assign(project.id, mateId)

        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun `should return true when assigning mate not yet assigned`() = runTest {
        // Given
        val mateId = UUID.randomUUID()
        val project = makeProject()
        coEvery { repository.get(project.id) } returns project
        coEvery { repository.update(any(), any()) } returns true

        // When
        val result = useCase.assign(project.id, mateId)

        // Then
        assertThat(result).isTrue()
        coVerify { repository.update(project.id, match { mateId in it.assignedMatesIds }) }
    }

    @Test
    fun `should return false when unassigning mate from non-existing project`() = runTest {
        // Given
        coEvery { repository.get(any()) } throws Exception()

        // When
        val result = useCase.assign(UUID.randomUUID(), UUID.randomUUID())

        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun `should return false when unassigning mate not in list`() = runTest {
        // Given
        val project = makeProject()
        val mateId = UUID.randomUUID()
        coEvery { repository.get(project.id) } returns project

        // When
        val result = useCase.assign(project.id, mateId)

        // Then
        assertThat(result).isFalse()
    }

//    @Test
//    fun `should return true when unassigning an existing mate`() {
//        // Given
//        val mateId = UUID.randomUUID()
//        val project = makeProject().apply { assignedMatesIds.add(mateId) }
//        every { repository.get(project.id) } returns project
//        every { repository.update(any(), any()) } returns true
//
//        // When
//        val result = useCase(project.id, mateId)
//
//        // Then
//        assertThat(result).isTrue()
//        verify { repository.update(project.id, match { mateId !in it.assignedMatesIds }) }
//    }

    private fun makeProject(id: UUID = UUID.randomUUID()): Project {
        return Project(
            id = id,
            name = "Dummy",
            assignedMatesIds = mutableListOf(),
            allowedStatesIds = mutableListOf(),
            creationDate = LocalDateTime(2023, 10, 7, 3, 30, 0)
        )
    }

}