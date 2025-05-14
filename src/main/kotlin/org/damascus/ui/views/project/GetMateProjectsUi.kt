package org.damascus.ui.views.project

import org.damascus.logic.model.User
import org.damascus.logic.usecase.project.ManageProjectUseCase
import org.damascus.ui.io.Display
import org.damascus.ui.util.printProjectTable

class GetMateProjectsUi(
    private val display: Display,
    private val manageProjectUseCase: ManageProjectUseCase,
) {
    operator fun invoke(currentMate: User) {
        val projects = manageProjectUseCase.getMateProjects(currentMate.id)
        if (projects.isEmpty()) {
            display.writeError(errorMessage = "You Are not Assigned to any Project.")
        } else projects.printProjectTable()
    }
}