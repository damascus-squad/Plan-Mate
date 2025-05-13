package org.damascus.ui.views.project

import logic.model.Project
import logic.model.User
import org.damascus.logic.usecase.auth.GetAllMatesUseCase
import org.damascus.ui.views.user.SelectMateUi
import ui.io.Display
import ui.util.UiAction
import ui.util.printProjectDetails

class UpdateProjectUi(
    private val display: Display,
    private val updateProjectTitleUi: UpdateProjectTitleUi,
    private val assignMateToProjectUi: AssignMateToProjectUi,
    private val unAssignMateFromProjectUi: UnAssignMateFromProjectUi,
    private val getAllMatesUseCase: GetAllMatesUseCase,
    private val selectMateUi: SelectMateUi,
) {
    operator fun invoke(currentUser: User, currentProject: Project) {
        currentProject.printProjectDetails()

        display.displayMenu(
            uiActionList = listOf(
                UiAction(
                    name = "Title",
                    action = { updateProjectTitleUi(currentProject, currentUser) }
                ),

                UiAction(
                    name = "Assign Mate",
                    action = {
                        val selectedMate = selectMateUi(getAllMatesUseCase())
                        if (selectedMate != null) assignMateToProjectUi(currentProject, selectedMate, currentUser)
                        else display.write(prompt = "❗ No mate selected for assignment.")
                    }
                ),
                UiAction(
                    name = "Remove Mate",
                    action = {
                        val selectedMate = selectMateUi(getAllMatesUseCase())
                        if (selectedMate != null) unAssignMateFromProjectUi(currentProject, selectedMate, currentUser)
                        else display.write(prompt = "❗ No mate selected for Remove.")
                    }
                ),
            ),
            menuTitle = "Select the field you want to update:"
        )
    }
}