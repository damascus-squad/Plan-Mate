package org.damascus.ui.views.project

import logic.model.User
import logic.usecase.project.GetMateProjectsUseCase
import ui.io.Display
import ui.util.printProjectTable

class GetMateProjectsUi(
    private val display: Display,
    private val getMateProjectsUseCase: GetMateProjectsUseCase,
) {
    operator fun invoke(currentMate: User) {
        val projects = getMateProjectsUseCase(currentMate.id)
        if (projects.isEmpty()) {
            display.writeError(errorMessage = "You Are not Assigned to any Project.")
        }
        else projects.printProjectTable()
    }
}