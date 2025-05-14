package org.damascus.ui.views.project

import org.damascus.logic.usecase.project.ManageProjectUseCase
import org.damascus.ui.io.Display
import org.damascus.ui.util.printProjectTable

class GetAdminProjectsUi(
    private val display: Display,
    private val manageProjectUseCase: ManageProjectUseCase,
) {
    operator fun invoke(): Boolean {
        val projects = manageProjectUseCase.getAllProjects()
        if (projects.isEmpty()) {
            display.writeError(errorMessage = "No projects available. Please Create Project first..")
            return false
        }
        else {
            projects.printProjectTable()
            return true
        }
    }
}