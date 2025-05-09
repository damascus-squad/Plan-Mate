package org.damascus.ui.views.project

import logic.usecase.project.GetAdminProjectsUseCase
import ui.io.Display
import ui.util.printProjectTable

class GetAdminProjectsUi(
    private val display: Display,
    private val getAdminProjectsUseCase: GetAdminProjectsUseCase,
) {
    operator fun invoke() {
        val projects = getAdminProjectsUseCase()
        if (projects.isEmpty()) {
            display.writeError(errorMessage = "No projects available.")
        }
        projects.printProjectTable()
    }
}