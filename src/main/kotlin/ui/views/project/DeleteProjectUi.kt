package org.damascus.ui.views.project

import logic.exception.ProjectNotFoundException
import logic.usecase.project.DeleteProjectUseCase
import logic.usecase.project.GetAdminProjectsUseCase
import ui.io.Display
import ui.io.InputReader
import ui.views.project.SelectProjectUi

class DeleteProjectUi (
    private val inputReader: InputReader,
    private val display: Display,
    private val deleteProjectUseCase: DeleteProjectUseCase,
    private val getAdminProjectsUi: GetAdminProjectsUi,
    private val getAdminProjectsUseCase: GetAdminProjectsUseCase,
    private val selectProjectUi: SelectProjectUi
){
    operator fun invoke () {
        getAdminProjectsUi()
        val currentProject = selectProjectUi(getAdminProjectsUseCase())

        try {
            val confirm = inputReader.readBoolean()
            if (confirm) {
                deleteProjectUseCase(currentProject.id)
                display.write(prompt = "🗑️ Project ${currentProject.id} deleted successfully!")
            } else {
                display.writeError(errorMessage = "❌ Project deletion canceled.")
            }
        } catch (e: ProjectNotFoundException) {
            display.writeError(errorMessage = " ❌ ${e.message}")
        }
    }
}