package ui.views.project

import logic.model.Project
import logic.usecase.project.GetProjectUseCase
import ui.io.Display
import ui.io.InputReader

class SelectProjectUi(
    private val consoleUserInput: InputReader,
    private val display: Display,
    private val getProjectUseCase: GetProjectUseCase
){
    operator fun invoke(projects: List<Project>): Project {
        val choice = consoleUserInput.readInt(
            prompt = "Enter project number to select: ",
            min = 1,
            max = projects.size
        )

        val selectedProject = getProjectUseCase(projects[choice - 1].id)
        display.write(prompt = "You selected: ${selectedProject.name} (ID: ${selectedProject.id})")
        return selectedProject
    }
}
