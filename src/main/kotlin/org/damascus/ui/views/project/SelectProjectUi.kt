package org.damascus.ui.views.project

import org.damascus.logic.model.Project
import org.damascus.logic.usecase.project.ManageProjectUseCase
import org.damascus.ui.io.Display
import org.damascus.ui.io.InputReader

class SelectProjectUi(
    private val consoleUserInput: InputReader,
    private val display: Display,
    private val manageProjectUseCase: ManageProjectUseCase,
) {
    operator suspend fun invoke(projects: List<Project>): Project {

        val choice = consoleUserInput.readInt(
            prompt = "Enter project number to select",
            min = 1,
            max = projects.size
        )

        val selectedProject = manageProjectUseCase.getProject(projects[choice - 1].id)
        display.write(prompt = "You selected: ${selectedProject.name} Project")
        return selectedProject
    }
}
