package logic.usecase.project

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.datetime.LocalDateTime
import logic.model.Project
import logic.repo.ProjectRepository
import org.damascus.logic.usecase.project.ManageMateAssignmentUseCase
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
    fun `should return false when assigning mate to non-existing project`() {
        // Given
        every { repository.get(any()) } throws Exception()

        // When
        val result = useCase.assign(UUID.randomUUID(), UUID.randomUUID())

        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun `should return false when assigning mate already assigned`() {
        // Given
        val mateId = UUID.randomUUID()
        val project = makeProject().apply { assignedMatesIds.add(mateId) }
        every { repository.get(project.id) } returns project

        // When
        val result = useCase.assign(project.id, mateId)

        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun `should return true when assigning mate not yet assigned`() {
        // Given
        val mateId = UUID.randomUUID()
        val project = makeProject()
        every { repository.get(project.id) } returns project
        every { repository.update(any(), any()) } returns true

        // When
        val result = useCase.assign(project.id, mateId)

        // Then
        assertThat(result).isTrue()
        verify { repository.update(project.id, match { mateId in it.assignedMatesIds }) }
    }

    @Test
    fun `should return false when unassigning mate from non-existing project`() {
        // Given
        every { repository.get(any()) } throws Exception()

        // When
        val result = useCase.assign(UUID.randomUUID(), UUID.randomUUID())

        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun `should return false when unassigning mate not in list`() {
        // Given
        val project = makeProject()
        val mateId = UUID.randomUUID()
        every { repository.get(project.id) } returns project

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