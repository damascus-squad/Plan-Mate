package org.damascus.ui.views.user

import org.damascus.logic.model.User
import org.damascus.ui.io.Display
import org.damascus.ui.util.UiAction

class MateManagementUi(
    private val display: Display,
    private val createMateUi: CreateMateUi,
    private val getAllMatesUi: GetAllMatesUi
) {

    operator fun invoke(admin: User) {
        val actions = listOf(
            UiAction("👤 Create New Mate", { createMateUi(admin) }),
            UiAction("📋 View All Mates", { getAllMatesUi() }),
        )

        display.displayMenu(
            uiActionList = actions,
            menuTitle = "Mate Management"
        )
    }
}
