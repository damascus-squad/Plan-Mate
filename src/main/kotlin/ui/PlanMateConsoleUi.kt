package ui

import logic.model.UserRole
import org.damascus.ui.views.admin.AdminDashboardUi
import org.damascus.ui.views.user.MateDashboardUi
import ui.views.LoginView

class PlanMateConsoleUi(
    private val loginView: LoginView,
    private val adminDashboardUi: AdminDashboardUi,
    private val mateDashboardUi: MateDashboardUi
) {

    fun start() {
        while (true) {
            val user = loginView.getLoggedUser()
            if (user.userRole == UserRole.ADMIN) {
                adminDashboardUi(currentUser = user)
            }else{
                mateDashboardUi(currentUser = user)
            }
        }
    }


    private companion object {
        const val MENU_TITLE = "Welcome to The Plan Mate App"
    }
}
