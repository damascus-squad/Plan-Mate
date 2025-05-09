package org.damascus.ui.views.project

import logic.model.User
import logic.usecase.project.GetAdminProjectsUseCase
import org.damascus.ui.views.admin.SelectMateUi
import ui.io.Display
import ui.util.UiAction
import ui.views.project.SelectProjectUi

class UpdateProjectUi(
    private val display: Display,
    private val updateProjectTitleUi: UpdateProjectTitleUi,
    private val assignMateToProjectUi: AssignMateToProjectUi,
    private val unAssignMateFromProjectUi: UnAssignMateFromProjectUi,
    private val selectProjectUi: SelectProjectUi,
    private val getAdminProjectsUi: GetAdminProjectsUi,
    private val selectMateUi: SelectMateUi,
    private val getAdminProjectsUseCase: GetAdminProjectsUseCase
) {
    operator fun invoke(currentUser:User) {
        getAdminProjectsUi()
        val currentProject = selectProjectUi(getAdminProjectsUseCase())

        display.displayMenu(
            listOf(
                UiAction("Title") { updateProjectTitleUi(currentProject,currentUser)},
                UiAction("Assign Mate") { assignMateToProjectUi(currentProject,selectMateUi()) },
                UiAction("Remove Mate") { unAssignMateFromProjectUi(currentProject, selectMateUi()) },
            ),
            menuTitle = "\nSelect the field you want to update:"
        )
    }
}