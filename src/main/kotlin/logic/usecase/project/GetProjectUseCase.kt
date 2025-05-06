package logic.usecase.project

import logic.model.Project
import logic.repo.ProjectRepository
import java.util.*

class GetProjectUseCase(private val repository: ProjectRepository) {
    operator fun invoke(projectId: UUID): Project {
        return repository.get(projectId)
    }
}
