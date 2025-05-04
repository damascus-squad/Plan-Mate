package logic.usecase.project

import logic.repo.ProjectRepository
import java.util.*

class ModifyMateAssignmentUseCase(
    private val repository: ProjectRepository
) {

    operator fun invoke(
        projectId: UUID,
        mateId: UUID,
        shouldAssign: Boolean
    ): Boolean {
        val project = try {
            repository.get(projectId)
        } catch (e: Exception) {
            return false
        }

        val mates = project.assignedMatesIds.toMutableList()
        val alreadyAssigned = mateId in mates

        if (shouldAssign && alreadyAssigned) return false
        if (!shouldAssign && !alreadyAssigned) return false

        if (shouldAssign) mates.add(mateId) else mates.remove(mateId)

        val updatedProject = project.copy(assignedMatesIds = mates)
        return repository.update(projectId, updatedProject)
    }
}
