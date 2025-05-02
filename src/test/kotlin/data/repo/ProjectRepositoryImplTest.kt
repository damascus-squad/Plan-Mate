package data.repo

import io.mockk.mockk
import logic.model.Project
import logic.model.Mate
import org.damascus.data.DataSource
import org.damascus.data.repo.ProjectRepositoryImpl
import com.google.common.truth.Truth.assertThat
import kotlinx.datetime.LocalDateTime
import org.damascus.data.repo.ProjectNotFoundException
import org.junit.jupiter.api.Test
import io.mockk.every
import io.mockk.justRun
import io.mockk.verify
import org.damascus.logic.model.Role
import org.junit.jupiter.api.BeforeEach
import java.util.*

class ProjectRepositoryImplTest {


    lateinit var dataSource: DataSource<Project>
    lateinit var repo: ProjectRepositoryImpl

    @BeforeEach
    fun setup() {
        dataSource = mockk(relaxed = true)
        repo = ProjectRepositoryImpl(dataSource)
    }

    @Test
    fun `should return false when create an existing project`() {
        // Given
        val project = makeProject(UUID.randomUUID())
        every { dataSource.read() } returns listOf(project)
        justRun { dataSource.write(any<Project>()) }

        // When
        val result = repo.create(project)

        // Then
        assertThat(result).isFalse()
        verify(exactly = 0) { dataSource.write(any<Project>()) }
    }

    @Test
    fun `should return true when create a new project`() {
        // Given
        val newProject = makeProject(UUID.randomUUID())
        every { dataSource.read() } returns listOf()
        justRun { dataSource.write(newProject) }

        // When
        val result = repo.create(newProject)

        // Then
        assertThat(result).isTrue()
        verify { dataSource.write(newProject) }
    }

    @Test
    fun `should return false when update a non existing project`() {
        // Given
        val project = makeProject(UUID.randomUUID())
        every { dataSource.read() } returns listOf()

        // When
        val result = repo.update(project.id, project)

        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun `should return true when update an existing project`() {
        // Given
        val project = makeProject(UUID.randomUUID())
        every { dataSource.read() } returns listOf(project)
        justRun { dataSource.update(project.id, project) }

        // When
        val result = repo.update(project.id, project)

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `should return true when project is updated successfully`() {
        // Given
        val project1 = makeProject(UUID.randomUUID())
        val project2 = makeProject(UUID.randomUUID())
        val updatedProject = makeProject(project1.id)
        every { dataSource.read() } returns listOf(project1, project2)
        justRun { dataSource.update(project1.id, updatedProject) }

        // When
        val result = repo.update(project1.id, updatedProject)

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `should return false when delete a non existing project`() {
        // Given
        val project = makeProject(UUID.randomUUID())
        every { dataSource.read() } returns listOf()

        // When
        val result = repo.delete(project.id)

        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun `should return true when delete an existing project`() {
        // Given
        val project = makeProject(UUID.randomUUID())
        every { dataSource.read() } returns listOf(project)
        justRun { dataSource.delete(project.id) }

        // When
        val result = repo.delete(project.id)

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `should return false when project not exist`() {
        // Given
        val project = makeProject(UUID.randomUUID())
        every { dataSource.read() } returns listOf()

        // When
        val result = repo.exists(project.id)

        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun `should return true when project exist`() {
        // Given
        val project = makeProject(UUID.randomUUID())
        every { dataSource.read() } returns listOf(project)

        // When
        val result = repo.exists(project.id)

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `should return project when project exist`() {
        // Given
        val project = makeProject(UUID.randomUUID())
        every { dataSource.read() } returns listOf(project)

        // When
        val result = repo.get(project.id)

        // Then
        assertThat(result).isEqualTo(project)
    }

    @Test
    fun `should throw ProjectNotFoundException when project does not exist`() {
        // Given
        val nonExistentId = UUID.randomUUID()
        every { dataSource.read() } returns listOf()

        // When
        val exception = org.junit.jupiter.api.assertThrows<ProjectNotFoundException> {
            repo.get(nonExistentId)
        }

        // Then
        assertThat(exception.message).contains(nonExistentId.toString())
    }

    @Test
    fun `should return empty list when no projects exist`() {
        // Given
        every { dataSource.read() } returns listOf()

        // When
        val result = repo.getAll()

        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun `should return list of projects when projects exist`() {
        // Given
        val project1 = makeProject(UUID.randomUUID())
        val project2 = makeProject(UUID.randomUUID())
        val twoProjects = listOf(project1, project2)
        every { dataSource.read() } returns twoProjects

        // When
        val result = repo.getAll()

        // Then
        assertThat(result).isEqualTo(twoProjects)
    }

    @Test
    fun `should return false when assign mate to a project not exist`() {
        // Given
        val mate = makeMate(UUID.randomUUID())
        every { dataSource.read() } returns listOf()

        // When
        val result = repo.assignMate(UUID.randomUUID(), mate.id)

        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun `should return false when assign a mate already assigned to the project`() {
        // Given
        val project = makeProject(UUID.randomUUID())
        val mate = makeMate(UUID.randomUUID())
        project.assignedMatesIds.add(mate.id)
        every { dataSource.read() } returns listOf(project)

        // When
        val result = repo.assignMate(project.id, mate.id)

        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun `should return true when assign an existing mate to a new existing project`() {
        // Given
        val project = makeProject(UUID.randomUUID())
        val mate = makeMate(UUID.randomUUID())
        every { dataSource.read() } returns listOf(project)
        justRun { dataSource.update(project.id, any()) }

        // When
        val result = repo.assignMate(project.id, mate.id)

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `should return true and save correct mapping when assigning mate`() {
        // Given
        val project1 = makeProject(UUID.randomUUID())
        val project2 = makeProject(UUID.randomUUID())
        val mate = makeMate(UUID.randomUUID())
        every { dataSource.read() } returns listOf(project1, project2)
        justRun { dataSource.update(project1.id, match { it.assignedMatesIds.contains(mate.id) }) }

        // When
        val result = repo.assignMate(project1.id, mate.id)

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `should return false when unassign mate to a project not exist`() {
        // Given
        every { dataSource.read() } returns listOf()

        // When
        val result = repo.unassignMate(UUID.randomUUID(), UUID.randomUUID())

        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun `should return false when unassign a mate that isn't assigned to the project`() {
        // Given
        val project = makeProject(UUID.randomUUID())
        val mate = makeMate(UUID.randomUUID())
        every { dataSource.read() } returns listOf(project)

        // When
        val result = repo.unassignMate(project.id, mate.id)

        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun `should return true when unassign an existing mate`() {
        // Given
        val project = makeProject(UUID.randomUUID())
        val mate = makeMate(UUID.randomUUID())
        project.assignedMatesIds.add(mate.id)
        every { dataSource.read() } returns listOf(project)
        justRun { dataSource.update(project.id, any()) }

        // When
        val result = repo.unassignMate(project.id, mate.id)

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `should return true and save correct mapping when unassigning mate`() {
        // Given
        val mate = makeMate(UUID.randomUUID())
        val project1 = makeProject(UUID.randomUUID()).apply { assignedMatesIds.add(mate.id) }
        val project2 = makeProject(UUID.randomUUID()).apply { assignedMatesIds.add(UUID.randomUUID()) }
        every { dataSource.read() } returns listOf(project1, project2)
        justRun { dataSource.update(project1.id, match { !it.assignedMatesIds.contains(mate.id) }) }

        // When
        val result = repo.unassignMate(project1.id, mate.id)

        // Then
        assertThat(result).isTrue()
    }

}

fun makeMate(mateID: UUID): Mate {
    return Mate(mateID, "name", "password", Role.MATE)
}

fun makeProject(projectID: UUID): Project {
    return Project(
        projectID,
        "name",
        mutableListOf(),
        LocalDateTime(2023, 10, 7, 3, 30, 0),
    )
}
