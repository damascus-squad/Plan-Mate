package logic.usecase.project

import logic.model.Project
import logic.repo.ProjectRepository
import java.util.UUID

class UpdateProjectUseCase(private val repository: ProjectRepository) {
    operator fun invoke(projectId: UUID, project: Project): Boolean {
        return repository.update(projectId, project)
    }
}