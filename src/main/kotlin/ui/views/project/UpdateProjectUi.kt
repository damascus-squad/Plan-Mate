package org.damascus.ui.views.project

import logic.model.Project
import logic.model.User
import org.damascus.logic.usecase.auth.GetAllMatesUseCase
import org.damascus.ui.views.user.SelectMateUi
import ui.io.Display
import ui.util.UiAction

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
            listOf(
                UiAction("Title") { updateProjectTitleUi(currentProject, currentUser) },
                UiAction("Assign Mate") {
                    assignMateToProjectUi(
                        currentProject,
                        selectMateUi(getAllMatesUseCase()).id
                    )
                },
                UiAction("Remove Mate") {
                    unAssignMateFromProjectUi(
                        currentProject,
                        selectMateUi(getAllMatesUseCase()).id
                    )
                },
            ),
            menuTitle = "\nSelect the field you want to update:"
        )
    }
}