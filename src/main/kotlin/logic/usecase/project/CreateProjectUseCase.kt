package logic.usecase.project

import logic.model.Project
import logic.model.TaskState
import logic.repo.ProjectRepository
import logic.repo.TaskStateRepository
import java.util.*

class CreateProjectUseCase(
    private val projectRepo: ProjectRepository,
    private val taskStateRepo :TaskStateRepository
) {
    operator fun invoke(project: Project): Boolean {
        if (projectRepo.exists(project.id)) return false

        val defaultStates = createDefaultStates()
        defaultStates.forEach {
            project.allowedStatesIds.add(taskStateRepo.create(it).id)
        }

        return projectRepo.create(project)
    }

    private fun createDefaultStates(): List<String> {
        return listOf("TODO", "IN PROGRESS", "DONE")
    }
}