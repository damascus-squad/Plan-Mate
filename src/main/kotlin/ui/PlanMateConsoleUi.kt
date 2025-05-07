package ui

import logic.model.UserRole
import org.damascus.ui.views.admin.AdminDashboardView
import ui.views.LoginView

class PlanMateConsoleUi(
    private val loginView: LoginView,
    private val adminDashboardView: AdminDashboardView
) {

    fun start() {
        while (true) {
            val user = loginView.getLoggedUser()
            if (user.userRole == UserRole.ADMIN) {
                adminDashboardView.showDashboard(user)
            }
        }
    }


    private companion object {
        const val MENU_TITLE = "Welcome to The Plan Mate App"
    }
}
