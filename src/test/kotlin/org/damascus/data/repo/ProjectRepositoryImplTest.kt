package org.damascus.data.repo

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDateTime
import org.damascus.data.dto.ProjectDTO
import org.damascus.data.mapper.toDto
import org.damascus.data.mapper.toModel
import org.damascus.logic.exception.ProjectNotFoundException
import org.damascus.logic.repo.DataSource
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class ProjectRepositoryImplTest {

    lateinit var dataSource: DataSource<ProjectDTO>
    lateinit var repo: ProjectRepositoryImpl

    @BeforeEach
    fun setup() {
        dataSource = mockk(relaxed = true)
        repo = ProjectRepositoryImpl(dataSource)
    }

    @Test
    fun `create function should return false when create an existing project`() = runTest {
        // Given
        val project = makeProject(UUID.randomUUID())
        coEvery { dataSource.read() } returns listOf(project)

        // When
        val result = repo.create(project.toModel())

        // Then
        assertThat(result).isFalse()
        coVerify(exactly = 0) { dataSource.write(any<ProjectDTO>()) }
    }

    @Test
    fun `create function should return true when create a new project`() = runTest {
        // Given
        val newProject = makeProject(UUID.randomUUID())
        coEvery { dataSource.read() } returns listOf()

        // When
        val result = repo.create(newProject.toModel())

        // Then
        assertThat(result).isTrue()
        coVerify { dataSource.write(newProject) }
    }

    @Test
    fun `update function should return false when update a non existing project`() = runTest {
        // Given
        val project = makeProject(UUID.randomUUID())
        coEvery { dataSource.read() } returns listOf()

        // When
        val result = repo.update(project.id, project.toModel())

        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun `update function should return true when project is updated successfully`() = runTest {
        // Given
        val project1 = makeProject(UUID.randomUUID())
        val project2 = makeProject(UUID.randomUUID())
        val updatedProject = makeProject(project1.id)
        coEvery { dataSource.read() } returns listOf(project1, project2)

        // When
        val result = repo.update(project1.id, updatedProject.toModel())

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `delete function should return false when delete a non existing project`() = runTest {
        // Given
        val project = makeProject(UUID.randomUUID())
        coEvery { dataSource.read() } returns listOf()

        // When
        val result = repo.delete(project.id)

        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun `delete function should return true when delete an existing project`() = runTest {
        // Given
        val project = makeProject(UUID.randomUUID())
        coEvery { dataSource.read() } returns listOf(project)

        // When
        val result = repo.delete(project.id)

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `exists function should return false when project not exist`() = runTest {
        // Given
        val project = makeProject(UUID.randomUUID())
        coEvery { dataSource.read() } returns listOf()

        // When
        val result = repo.exists(project.id)

        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun `exists function should return true when project exist`() = runTest {
        // Given
        val project = makeProject(UUID.randomUUID())
        coEvery { dataSource.read() } returns listOf(project)

        // When
        val result = repo.exists(project.id)

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `get function should return project when project exist`() = runTest {
        // Given
        val project = makeProject(UUID.randomUUID())
        coEvery { dataSource.read() } returns listOf(project)

        // When
        val result = repo.get(project.id)

        // Then
        assertThat(result.toDto()).isEqualTo(project)
    }

    @Test
    fun `get should throw ProjectNotFoundException when project does not exist`() = runTest {
        // Given
        val nonExistentId = UUID.randomUUID()
        coEvery { dataSource.read() } returns listOf()

        // When & Then
        assertThrows<ProjectNotFoundException> {
            repo.get(nonExistentId)
        }
    }

    @Test
    fun `getAll function should return empty list when no projects exist`() = runTest {
        // Given
        coEvery { dataSource.read() } returns listOf()

        // When
        val result = repo.getAll()

        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun `getAll function should return list of projects when projects exist`() = runTest {
        // Given
        val project1 = makeProject(UUID.randomUUID())
        val project2 = makeProject(UUID.randomUUID())
        val twoProjects = listOf(project1, project2)
        coEvery { dataSource.read() } returns twoProjects

        // When
        val result = repo.getAll()

        // Then
        assertThat(result.map { it.toDto() }).isEqualTo(twoProjects)
    }

    @Test
    fun `getAllProjectsByMateId function should return empty list when no projects are assigned to the mate`() =
        runTest {
            // Given
            val mateId = UUID.randomUUID()
            coEvery { dataSource.read() } returns listOf()

            // When
            val result = repo.getAllProjectsByMateId(mateId)

            // Then
            assertThat(result).isEmpty()
        }

    @Test
    fun `getAllProjectsByMateId function should return a list of projects assigned to the mate`() = runTest {
        // Given
        val mateId = UUID.randomUUID()
        val project1 = makeProject(UUID.randomUUID()).apply { assignedMatesIds.add(mateId) }
        val project2 = makeProject(UUID.randomUUID()).apply { assignedMatesIds.add(mateId) }
        coEvery { dataSource.read() } returns listOf(project1, project2)

        // When
        val result = repo.getAllProjectsByMateId(mateId)

        // Then
        assertThat(result.map { it.toDto() }).containsExactly(project1, project2)
    }

    @Test
    fun `getAllProjectsByMateId function should return empty list when no project contains the mate`() = runTest {
        // Given
        val mateId = UUID.randomUUID()
        val project1 = makeProject(UUID.randomUUID()) // Mate is not assigned
        val project2 = makeProject(UUID.randomUUID()) // Mate is not assigned
        coEvery { dataSource.read() } returns listOf(project1, project2)

        // When
        val result = repo.getAllProjectsByMateId(mateId)

        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun `getAllProjectsByMateId should read from dataSource`() = runTest {
        // Given
        val mateId = UUID.randomUUID()
        val project = makeProject(UUID.randomUUID()).apply { assignedMatesIds.add(mateId) }
        coEvery { dataSource.read() } returns listOf(project)

        // When
        repo.getAllProjectsByMateId(mateId)

        // Then
        coVerify { dataSource.read() }
    }

    private fun makeProject(id: UUID) = ProjectDTO(
        id = id,
        "name",
        mutableListOf(),
        mutableListOf(),
        LocalDateTime(2023, 10, 7, 3, 30, 0),
    )


}
