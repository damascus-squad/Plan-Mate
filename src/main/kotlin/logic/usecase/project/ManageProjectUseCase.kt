package org.damascus.logic.usecase.project

import logic.model.Project
import logic.repo.ProjectRepository
import logic.repo.TaskStateRepository
import java.util.*


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
