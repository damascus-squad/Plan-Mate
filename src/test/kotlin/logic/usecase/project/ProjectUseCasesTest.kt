package logic.usecase.project

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.datetime.LocalDateTime
import logic.model.Project
import logic.repo.ProjectRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class ProjectUseCasesTest {

    lateinit var repository: ProjectRepository
    lateinit var project: Project

    @BeforeEach
    fun setup() {
        repository = mockk(relaxed = true)
        project = makeProject(UUID.randomUUID())
    }

    @Test
    fun `create use case should return false if project already exists`() {
        every { repository.exists(project.id) } returns true

        val result = CreateProjectUseCase(repository).invoke(project)

        assertThat(result).isFalse()
    }

    @Test
    fun `create use case should return true if project is new`() {
        every { repository.exists(project.id) } returns false
        every { repository.create(project) } returns true

        val result = CreateProjectUseCase(repository).invoke(project)

        assertThat(result).isTrue()
        verify { repository.create(project) }
    }

    @Test
    fun `update use case should return true if project updated`() {
        every { repository.update(project.id, project) } returns true

        val result = UpdateProjectUseCase(repository).invoke(project.id, project)

        assertThat(result).isTrue()
    }

    @Test
    fun `update use case should return false if project does not exist`() {
        every { repository.update(project.id, project) } returns false

        val result = UpdateProjectUseCase(repository).invoke(project.id, project)

        assertThat(result).isFalse()
    }

    @Test
    fun `delete use case should return true when deletion is successful`() {
        every { repository.delete(project.id) } returns true

        val result = DeleteProjectUseCase(repository).invoke(project.id)

        assertThat(result).isTrue()
    }

    @Test
    fun `delete use case should return false when project does not exist`() {
        every { repository.delete(project.id) } returns false

        val result = DeleteProjectUseCase(repository).invoke(project.id)

        assertThat(result).isFalse()
    }

    @Test
    fun `get project use case should return correct project`() {
        every { repository.get(project.id) } returns project

        val result = GetProjectUseCase(repository).invoke(project.id)

        assertThat(result).isEqualTo(project)
    }

    @Test
    fun `get all projects use case should return list of projects`() {
        val project2 = makeProject(UUID.randomUUID())
        every { repository.getAll() } returns listOf(project, project2)

        val result = GetAllProjectsUseCase(repository).invoke()

        assertThat(result).containsExactly(project, project2)
    }

    @Test
    fun `get all projects use case should return empty list when no projects exist`() {
        every { repository.getAll() } returns listOf()

        val result = GetAllProjectsUseCase(repository).invoke()

        assertThat(result).isEmpty()
    }

    @Test
    fun `get all projects by mate id should return list of matching projects`() {
        val mateId = UUID.randomUUID()
        val projectWithMate = makeProject(UUID.randomUUID()).apply { assignedMatesIds.add(mateId) }
        every { repository.getAllProjectsByMateId(mateId) } returns listOf(projectWithMate)

        val result = GetAllProjectsByMateIdUseCase(repository).invoke(mateId)

        assertThat(result).containsExactly(projectWithMate)
    }

    @Test
    fun `get all projects by mate id should return empty list if no matches`() {
        val mateId = UUID.randomUUID()
        every { repository.getAllProjectsByMateId(mateId) } returns listOf()

        val result = GetAllProjectsByMateIdUseCase(repository).invoke(mateId)

        assertThat(result).isEmpty()
    }

    @Test
    fun `check project exists use case should return true if project exists`() {
        every { repository.exists(project.id) } returns true

        val result = CheckProjectExistsUseCase(repository).invoke(project.id)

        assertThat(result).isTrue()
    }

    @Test
    fun `check project exists use case should return false if project does not exist`() {
        every { repository.exists(project.id) } returns false

        val result = CheckProjectExistsUseCase(repository).invoke(project.id)

        assertThat(result).isFalse()
    }
}

fun makeProject(id: UUID): Project {
    return Project(
        id = id,
        name = "Test Project",
        assignedMatesIds = mutableListOf(),
        allowedStatesIds = mutableListOf(),
        creationDate = LocalDateTime(2023, 10, 7, 3, 30, 0)
    )
}
