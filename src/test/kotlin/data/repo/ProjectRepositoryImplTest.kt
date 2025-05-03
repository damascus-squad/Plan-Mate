package data.repo

import io.mockk.mockk
import logic.model.Project
import org.damascus.data.repo.ProjectRepositoryImpl
import com.google.common.truth.Truth.assertThat
import kotlinx.datetime.LocalDateTime
import org.damascus.data.repo.ProjectNotFoundException
import org.junit.jupiter.api.Test
import io.mockk.every
import org.junit.jupiter.api.assertThrows
import io.mockk.verify
import logic.repo.DataSource
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
    fun `create function should return false when create an existing project`() {
        // Given
        val project = makeProject(UUID.randomUUID())
        every { dataSource.read() } returns listOf(project)

        // When
        val result = repo.create(project)

        // Then
        assertThat(result).isFalse()
        verify(exactly = 0) { dataSource.write(any<Project>()) }
    }

    @Test
    fun `create function should return true when create a new project`() {
        // Given
        val newProject = makeProject(UUID.randomUUID())
        every { dataSource.read() } returns listOf()

        // When
        val result = repo.create(newProject)

        // Then
        assertThat(result).isTrue()
        verify { dataSource.write(newProject) }
    }

    @Test
    fun `update function should return false when update a non existing project`() {
        // Given
        val project = makeProject(UUID.randomUUID())
        every { dataSource.read() } returns listOf()

        // When
        val result = repo.update(project.id, project)

        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun `update function should return true when project is updated successfully`() {
        // Given
        val project1 = makeProject(UUID.randomUUID())
        val project2 = makeProject(UUID.randomUUID())
        val updatedProject = makeProject(project1.id)
        every { dataSource.read() } returns listOf(project1, project2)

        // When
        val result = repo.update(project1.id, updatedProject)

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `delete function should return false when delete a non existing project`() {
        // Given
        val project = makeProject(UUID.randomUUID())
        every { dataSource.read() } returns listOf()

        // When
        val result = repo.delete(project.id)

        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun `delete function should return true when delete an existing project`() {
        // Given
        val project = makeProject(UUID.randomUUID())
        every { dataSource.read() } returns listOf(project)

        // When
        val result = repo.delete(project.id)

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `exists function should return false when project not exist`() {
        // Given
        val project = makeProject(UUID.randomUUID())
        every { dataSource.read() } returns listOf()

        // When
        val result = repo.exists(project.id)

        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun `exists function should return true when project exist`() {
        // Given
        val project = makeProject(UUID.randomUUID())
        every { dataSource.read() } returns listOf(project)

        // When
        val result = repo.exists(project.id)

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `get function should return project when project exist`() {
        // Given
        val project = makeProject(UUID.randomUUID())
        every { dataSource.read() } returns listOf(project)

        // When
        val result = repo.get(project.id)

        // Then
        assertThat(result).isEqualTo(project)
    }

    @Test
    fun `get should throw ProjectNotFoundException when project does not exist`() {
        // Given
        val nonExistentId = UUID.randomUUID()
        every { dataSource.read() } returns listOf()

        // When & Then
        assertThrows<ProjectNotFoundException> {
            repo.get(nonExistentId)
        }
    }

    @Test
    fun `getAll function should return empty list when no projects exist`() {
        // Given
        every { dataSource.read() } returns listOf()

        // When
        val result = repo.getAll()

        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun `getAll function should return list of projects when projects exist`() {
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
    fun `getAllProjectsByMateId function should return empty list when no projects are assigned to the mate`() {
        // Given
        val mateId = UUID.randomUUID()
        every { dataSource.read() } returns listOf()

        // When
        val result = repo.getAllProjectsByMateId(mateId)

        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun `getAllProjectsByMateId function should return a list of projects assigned to the mate`() {
        // Given
        val mateId = UUID.randomUUID()
        val project1 = makeProject(UUID.randomUUID()).apply { assignedMatesIds.add(mateId) }
        val project2 = makeProject(UUID.randomUUID()).apply { assignedMatesIds.add(mateId) }
        every { dataSource.read() } returns listOf(project1, project2)

        // When
        val result = repo.getAllProjectsByMateId(mateId)

        // Then
        assertThat(result).containsExactly(project1, project2)
    }

    @Test
    fun `getAllProjectsByMateId function should return empty list when no project contains the mate`() {
        // Given
        val mateId = UUID.randomUUID()
        val project1 = makeProject(UUID.randomUUID()) // Mate is not assigned
        val project2 = makeProject(UUID.randomUUID()) // Mate is not assigned
        every { dataSource.read() } returns listOf(project1, project2)

        // When
        val result = repo.getAllProjectsByMateId(mateId)

        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun `getAllProjectsByMateId should read from dataSource`() {
        // Given
        val mateId = UUID.randomUUID()
        val project = makeProject(UUID.randomUUID()).apply { assignedMatesIds.add(mateId) }
        every { dataSource.read() } returns listOf(project)

        // When
        repo.getAllProjectsByMateId(mateId)

        // Then
        verify { dataSource.read() }
    }

}

fun makeProject(id: UUID): Project {
    return Project(
        id = id,
        "name",
        mutableListOf(),
        LocalDateTime(2023, 10, 7, 3, 30, 0),
    )
}
