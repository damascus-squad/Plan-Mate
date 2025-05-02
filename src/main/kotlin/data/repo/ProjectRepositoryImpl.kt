package org.damascus.data.repo

import kotlinx.datetime.LocalDateTime
import logic.model.Project
import org.damascus.data.DataSource
import org.damascus.logic.repository.ProjectRepository
import java.util.*

class ProjectNotFoundException(projectId: UUID) :
    RuntimeException("Project with ID $projectId not found")

class ProjectRepositoryImpl(private val projectDataSource: DataSource<Project>): ProjectRepository {

    override fun create(project: Project) : Boolean{
        return true
    }
    override fun update(projectId: UUID, project: Project) : Boolean{
        return true
    }
    override fun delete(projectId: UUID) : Boolean{
        return true
    }
    override fun exists(projectId: UUID) : Boolean{
        return true
    }
    override fun get(projectId: UUID) : Project{
        return Project(
            id = UUID.randomUUID(),
            name = "Project",
            assignedMatesIds = mutableListOf(UUID.randomUUID()),
            creationDate = LocalDateTime(2024, 5, 1, 12, 0)
        )

    }
    override fun getAll(): List<Project> {
        return listOf()
    }
    override fun assignMate(projectId: UUID, mateId: UUID): Boolean{
        return true
    }
    override fun unassignMate(projectId: UUID, mateId: UUID): Boolean{
        return true
    }

}