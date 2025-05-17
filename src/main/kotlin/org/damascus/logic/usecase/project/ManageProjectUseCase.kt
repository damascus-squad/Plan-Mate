package org.damascus.logic.usecase.project

import org.damascus.logic.model.Project
import org.damascus.logic.repo.ProjectRepository
import org.damascus.logic.repo.TaskStateRepository
import org.koin.core.annotation.Single
import java.util.*

@Single
class ManageProjectUseCase(
    private val projectRepo: ProjectRepository,
    private val taskStateRepo: TaskStateRepository
) {
    suspend fun createProject(project: Project): Boolean {
        if (projectRepo.exists(project.id)) return false

        project.allowedStatesIds.addAll(getDefaultTaskStates())
        return projectRepo.create(project)
    }

    suspend fun getProject(projectId: UUID) = projectRepo.get(projectId)
    suspend fun getMateProjects(mateId: UUID) = projectRepo.getAllProjectsByMateId(mateId)
    suspend fun getAllProjects() = projectRepo.getAll()

    suspend fun updateProject(projectId: UUID, project: Project) = projectRepo.update(projectId, project)

    suspend fun deleteProject(projectId: UUID) = projectRepo.delete(projectId)

    private suspend fun getDefaultTaskStates() =
        listOf("TODO", "IN PROGRESS", "DONE").map { taskStateRepo.create(it).id }

}
