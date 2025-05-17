package org.damascus.ui

import org.damascus.logic.model.UserRole
import org.damascus.ui.io.Display
import org.damascus.ui.util.UiAction
import org.damascus.ui.views.user.LoginView
import org.damascus.ui.views.user.AdminDashboardUi
import org.damascus.ui.views.user.MateDashboardUi
import org.koin.core.annotation.Single

@Single
class PlanMateConsoleUi(
    private val loginView: LoginView,
    private val adminDashboardUi: AdminDashboardUi,
    private val mateDashboardUi: MateDashboardUi,
    private val display: Display
) {

    suspend fun start() {
        while (true) {
            display.displayMenu(
                listOf(
                    UiAction("Login", {
                        val user = loginView.getLoggedUser()
                        if (user.userRole == UserRole.ADMIN) {
                            adminDashboardUi(admin = user)
                        } else {
                            mateDashboardUi(currentUser = user)
                        }
                    })
                ),
                menuTitle = MENU_TITLE ,
                showBackOption = false,
            )
        }
    }

    private companion object{
        const val MENU_TITLE = "🔷 Welcome to PlanMate v1.0 🔷"
    }
}
