package org.damascus.ui.views.project

import org.damascus.logic.model.Project
import org.damascus.logic.model.User
import org.damascus.logic.usecase.auth.ManageMateUseCase
import org.damascus.ui.io.Display
import org.damascus.ui.util.UiAction
import org.damascus.ui.util.printProjectDetails
import org.damascus.ui.views.user.SelectMateUi
import org.koin.core.annotation.Single

@Single
class UpdateProjectUi(
    private val display: Display,
    private val updateProjectTitleUi: UpdateProjectTitleUi,
    private val assignMateToProjectUi: AssignMateToProjectUi,
    private val unAssignMateFromProjectUi: UnAssignMateFromProjectUi,
    private val manageMateUseCase: ManageMateUseCase,
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
                        val selectedMate = selectMateUi(manageMateUseCase.getAllMates())
                        if (selectedMate != null) assignMateToProjectUi(currentProject, selectedMate, currentUser)
                        else display.write(prompt = "❗ No mate selected for assignment.")
                    }
                ),
                UiAction(
                    name = "Remove Mate",
                    action = {
                        val selectedMate = selectMateUi(manageMateUseCase.getAllMates())
                        if (selectedMate != null) unAssignMateFromProjectUi(currentProject, selectedMate, currentUser)
                        else display.write(prompt = "❗ No mate selected for Remove.")
                    }
                ),
            ),
            menuTitle = "Select the field you want to update:"
        )
    }
}