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

        display.displayMenu(
            uiActionList = listOf(
                UiAction("Title") { updateProjectTitleUi(currentProject, currentUser) },
                UiAction("Assign Mate") {
                    val selectedMate = selectMateUi(getAllMatesUseCase())
                    if (selectedMate != null) {
                        assignMateToProjectUi(currentProject, selectedMate.id)
                    } else {
                        display.write(prompt = "❗ No mate selected for assignment.")
                    }
                },
                UiAction("Remove Mate") {
                    val selectedMate = selectMateUi(getAllMatesUseCase())
                    if (selectedMate != null) {
                        unAssignMateFromProjectUi(currentProject, selectedMate.id)
                    } else {
                        display.write(prompt = "❗ No mate selected for Remove.")
                    }
                },
            ),
            menuTitle = "\nSelect the field you want to update:"
        )
    }
}