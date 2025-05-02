package org.damascus.data.repo



import logic.model.Project
import org.damascus.data.DataSource
import org.damascus.logic.repository.ProjectRepository
import java.util.*


class ProjectNotFoundException(projectId: UUID) :
    RuntimeException("Project with ID $projectId not found")

class ProjectRepositoryImpl(private val projectDataSource: DataSource<Project>) : ProjectRepository {

    override fun create(project: Project): Boolean {
        val existingProjects = projectDataSource.read()
        if (existingProjects.any { it.id == project.id }) return false
        projectDataSource.write(project)
        return true
    }

    override fun update(projectId: UUID, project: Project): Boolean {
        val existingProjects = projectDataSource.read()
        if (existingProjects.none { it.id == projectId }) return false
        projectDataSource.update(projectId, project)
        return true
    }

    override fun delete(projectId: UUID): Boolean {
        val existingProjects = projectDataSource.read()
        if (existingProjects.none { it.id == projectId }) return false
        projectDataSource.delete(projectId)
        return true
    }

    override fun exists(projectId: UUID): Boolean {
        return projectDataSource.read().any { it.id == projectId }
    }

    override fun get(projectId: UUID): Project {
        return projectDataSource.read().firstOrNull { it.id == projectId }
            ?: throw ProjectNotFoundException(projectId)
    }

    override fun getAll(): List<Project> {
        return projectDataSource.read()
    }

    override fun assignMate(projectId: UUID, mateId: UUID): Boolean {
        val existingProjects = projectDataSource.read()

        val project = existingProjects.firstOrNull { it.id == projectId }
            ?: return false

        if (project.assignedMatesIds.contains(mateId))
            return false

        val updatedProject = project.copy(
            assignedMatesIds = project.assignedMatesIds.toMutableList().apply { add(mateId) }
        )

        projectDataSource.update(project.id, updatedProject)
        return true
    }

    override fun unassignMate(projectId: UUID, mateId: UUID): Boolean {
        val existingProjects = projectDataSource.read()

        val project = existingProjects.firstOrNull { it.id == projectId }
            ?: return false

        if (!project.assignedMatesIds.contains(mateId))
            return false

        val updatedProject = project.copy(
            assignedMatesIds = project.assignedMatesIds.toMutableList().apply { remove(mateId) }
        )

        projectDataSource.update(project.id, updatedProject)
        return true
    }
}