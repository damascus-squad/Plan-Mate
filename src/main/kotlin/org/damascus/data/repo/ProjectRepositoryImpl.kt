package org.damascus.data.repo

import org.damascus.data.dto.ProjectDTO
import org.damascus.data.mapper.toDto
import org.damascus.data.mapper.toModel
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
    private val dataSource: DataSource<ProjectDTO>
) : ProjectRepository {

    override suspend fun create(project: Project): Boolean {
        if (dataSource.read().any { it.id == project.id }) return false
        dataSource.write(project.toDto())
        return true
    }

    override suspend fun update(projectId: UUID, project: Project): Boolean {
        if (dataSource.read().none { it.id == projectId }) return false
        dataSource.update(projectId, project.toDto())
        return true
    }

    override suspend fun delete(projectId: UUID): Boolean {
        if (dataSource.read().none { it.id == projectId }) return false
        dataSource.delete(projectId)
        return true
    }

    override suspend fun exists(projectId: UUID): Boolean {
        return dataSource.read().any { it.id == projectId }
    }

    override suspend fun get(projectId: UUID): Project {
        return dataSource.read().firstOrNull { it.id == projectId }?.toModel()
            ?: throw ProjectNotFoundException(projectId)
    }

    override suspend fun getAll(): List<Project> {
        return dataSource.read().map { it.toModel() }
    }

    override suspend fun getAllProjectsByMateId(mateId: UUID): List<Project> {
        return dataSource.read().filter { mateId in it.assignedMatesIds }.map { it.toModel() }
    }
}