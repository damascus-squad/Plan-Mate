package logic.usecase.project

import logic.repo.ProjectRepository
import java.util.*

class AssignMateUseCase(
    private val repository: ProjectRepository
) {

    operator fun invoke(
        projectId: UUID,
        mateId: UUID,
    ): Boolean {
        val project = try {
            repository.get(projectId)
        } catch (e: Exception) {
            return false
        }

        val mates = project.assignedMatesIds.toMutableList()
        val alreadyAssigned = mateId !in mates

        if (!alreadyAssigned) mates.add(mateId)

        val updatedProject = project.copy(assignedMatesIds = mates)
        return repository.update(projectId, updatedProject)
    }
}
