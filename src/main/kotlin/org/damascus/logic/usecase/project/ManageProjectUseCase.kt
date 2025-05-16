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
    fun createProject(project: Project): Boolean {
        if (projectRepo.exists(project.id)) return false

        project.allowedStatesIds.addAll(getDefaultTaskStates())
        return projectRepo.create(project)
    }

    fun getProject(projectId: UUID) = projectRepo.get(projectId)
    fun getMateProjects(mateId: UUID) = projectRepo.getAllProjectsByMateId(mateId)
    fun getAllProjects() = projectRepo.getAll()

    fun updateProject(projectId: UUID, project: Project) = projectRepo.update(projectId, project)

    fun deleteProject(projectId: UUID) = projectRepo.delete(projectId)

    private fun getDefaultTaskStates() = listOf("TODO", "IN PROGRESS", "DONE").map { taskStateRepo.create(it).id }

}
