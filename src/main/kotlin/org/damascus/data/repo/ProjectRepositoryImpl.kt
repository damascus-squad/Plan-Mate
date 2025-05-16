package org.damascus.data.repo

import org.damascus.logic.exception.ProjectNotFoundException
import org.damascus.logic.model.Project
import org.damascus.logic.repo.DataSource
import org.damascus.logic.repo.ProjectRepository
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import java.util.*

@Single
class ProjectRepositoryImpl(
    @Named("projectDataSource")
    private val dataSource: DataSource<Project>
) : ProjectRepository {

    override fun create(project: Project): Boolean {
        if (dataSource.read().any { it.id == project.id }) return false
        dataSource.write(project)
        return true
    }

    override fun update(projectId: UUID, project: Project): Boolean {
        if (dataSource.read().none { it.id == projectId }) return false
        dataSource.update(projectId, project)
        return true
    }

    override fun delete(projectId: UUID): Boolean {
        if (dataSource.read().none { it.id == projectId }) return false
        dataSource.delete(projectId)
        return true
    }

    override fun exists(projectId: UUID): Boolean {
        return dataSource.read().any { it.id == projectId }
    }

    override fun get(projectId: UUID): Project {
        return dataSource.read().firstOrNull { it.id == projectId }
            ?: throw ProjectNotFoundException(projectId)
    }

    override fun getAll(): List<Project> {
        return dataSource.read()
    }

    override fun getAllProjectsByMateId(mateId: UUID): List<Project> {
        return dataSource.read().filter { mateId in it.assignedMatesIds }
    }
}