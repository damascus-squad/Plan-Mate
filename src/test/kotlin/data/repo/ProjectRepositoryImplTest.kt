
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
  val project = makeProject(UUID.randomUUID())
  every { dataSource.read() } returns listOf(project)
  justRun { dataSource.write(any<Project>()) }
  val result = repo.create(project)
  assertThat(result).isFalse()
  verify(exactly = 0) { dataSource.write(any<Project>()) }
 }

 @Test
 fun `should return true when create a new project`() {
  val newProject = makeProject(UUID.randomUUID())
  every { dataSource.read() } returns listOf()
  justRun { dataSource.write(newProject) }
  val result = repo.create(newProject)
  assertThat(result).isTrue()
  verify { dataSource.write(newProject) }
 }

 @Test
 fun `should return false when update a non existing project`() {
  val project = makeProject(UUID.randomUUID())
  every { dataSource.read() } returns listOf()
  val result = repo.update(project.id, project)
  assertThat(result).isFalse()
 }

 @Test
 fun `should return true when update an existing project`() {
  val project = makeProject(UUID.randomUUID())
  every { dataSource.read() } returns listOf(project)
  justRun { dataSource.update(project.id, project) }
  val result = repo.update(project.id, project)
  assertThat(result).isTrue()
 }

 @Test
 fun `should return true when project is updated successfully`() {
  val project1 = makeProject(UUID.randomUUID())
  val project2 = makeProject(UUID.randomUUID())
  val updatedProject = makeProject(project1.id)
  every { dataSource.read() } returns listOf(project1, project2)
  justRun { dataSource.update(project1.id, updatedProject) }
  val result = repo.update(project1.id, updatedProject)
  assertThat(result).isTrue()
 }

 @Test
 fun `should return false when delete a non existing project`() {
  val project = makeProject(UUID.randomUUID())
  every { dataSource.read() } returns listOf()
  val result = repo.delete(project.id)
  assertThat(result).isFalse()
 }

 @Test
 fun `should return true when delete an existing project`() {
  val project = makeProject(UUID.randomUUID())
  every { dataSource.read() } returns listOf(project)
  justRun { dataSource.delete(project.id) }
  val result = repo.delete(project.id)
  assertThat(result).isTrue()
 }

 @Test
 fun `should return false when project not exist`() {
  val project = makeProject(UUID.randomUUID())
  every { dataSource.read() } returns listOf()
  val result = repo.exists(project.id)
  assertThat(result).isFalse()
 }

 @Test
 fun `should return true when project exist`() {
  val project = makeProject(UUID.randomUUID())
  every { dataSource.read() } returns listOf(project)
  val result = repo.exists(project.id)
  assertThat(result).isTrue()
 }

 @Test
 fun `should return project when project exist`() {
  val project = makeProject(UUID.randomUUID())
  every { dataSource.read() } returns listOf(project)
  val result = repo.get(project.id)
  assertThat(result).isEqualTo(project)
 }

 @Test
 fun `should throw ProjectNotFoundException when project does not exist`() {
  val nonExistentId = UUID.randomUUID()
  every { dataSource.read() } returns listOf()
  val exception = org.junit.jupiter.api.assertThrows<ProjectNotFoundException> {
   repo.get(nonExistentId)
  }
  assertThat(exception.message).contains(nonExistentId.toString())
 }

 @Test
 fun `should return empty list when no projects exist`() {
  every { dataSource.read() } returns listOf()
  val result = repo.getAll()
  assertThat(result).isEmpty()
 }

 @Test
 fun `should return list of projects when projects exist`() {
  val project1 = makeProject(UUID.randomUUID())
  val project2 = makeProject(UUID.randomUUID())
  val twoProjects = listOf(project1, project2)
  every { dataSource.read() } returns twoProjects
  val result = repo.getAll()
  assertThat(result).isEqualTo(twoProjects)
 }

 @Test
 fun `should return false when assign mate to a project not exist`() {
  val mate = makeMate(UUID.randomUUID())
  every { dataSource.read() } returns listOf()
  val result = repo.assignMate(UUID.randomUUID(), mate.id)
  assertThat(result).isFalse()
 }

 @Test
 fun `should return false when assign a mate already assigned to the project`() {
  val project = makeProject(UUID.randomUUID())
  val mate = makeMate(UUID.randomUUID())
  project.assignedMatesIds.add(mate.id)
  every { dataSource.read() } returns listOf(project)
  val result = repo.assignMate(project.id, mate.id)
  assertThat(result).isFalse()
 }

 @Test
 fun `should return true when assign an existing mate to a new existing project`() {
  val project = makeProject(UUID.randomUUID())
  val mate = makeMate(UUID.randomUUID())
  every { dataSource.read() } returns listOf(project)
  justRun { dataSource.update(project.id, any()) }
  val result = repo.assignMate(project.id, mate.id)
  assertThat(result).isTrue()
 }

 @Test
 fun `should return true and save correct mapping when assigning mate`() {
  val project1 = makeProject(UUID.randomUUID())
  val project2 = makeProject(UUID.randomUUID())
  val mate = makeMate(UUID.randomUUID())
  every { dataSource.read() } returns listOf(project1, project2)
  justRun { dataSource.update(project1.id, match { it.assignedMatesIds.contains(mate.id) }) }
  val result = repo.assignMate(project1.id, mate.id)
  assertThat(result).isTrue()
 }

 @Test
 fun `should return false when unassign mate to a project not exist`() {
  every { dataSource.read() } returns listOf()
  val result = repo.unassignMate(UUID.randomUUID(), UUID.randomUUID())
  assertThat(result).isFalse()
 }

 @Test
 fun `should return false when unassign a mate that isn't assigned to the project`() {
  val project = makeProject(UUID.randomUUID())
  val mate = makeMate(UUID.randomUUID())
  every { dataSource.read() } returns listOf(project)
  val result = repo.unassignMate(project.id, mate.id)
  assertThat(result).isFalse()
 }

 @Test
 fun `should return true when unassign an existing mate`() {
  val project = makeProject(UUID.randomUUID())
  val mate = makeMate(UUID.randomUUID())
  project.assignedMatesIds.add(mate.id)
  every { dataSource.read() } returns listOf(project)
  justRun { dataSource.update(project.id, any()) }
  val result = repo.unassignMate(project.id, mate.id)
  assertThat(result).isTrue()
 }

 @Test
 fun `should return true and save correct mapping when unassigning mate`() {
  val mate = makeMate(UUID.randomUUID())
  val project1 = makeProject(UUID.randomUUID()).apply { assignedMatesIds.add(mate.id) }
  val project2 = makeProject(UUID.randomUUID()).apply { assignedMatesIds.add(UUID.randomUUID()) }
  every { dataSource.read() } returns listOf(project1, project2)
  justRun { dataSource.update(project1.id, match { !it.assignedMatesIds.contains(mate.id) }) }
  val result = repo.unassignMate(project1.id, mate.id)
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
