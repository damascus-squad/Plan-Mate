package ui

import logic.model.UserRole
import org.damascus.ui.views.admin.AdminDashboardUi
import org.damascus.ui.views.user.MateDashboardUi
import ui.io.Display
import ui.util.UiAction
import ui.views.LoginView

class PlanMateConsoleUi(
    private val loginView: LoginView,
    private val adminDashboardUi: AdminDashboardUi,
    private val mateDashboardUi: MateDashboardUi,
    private val display: Display
) {

    fun start() {
        while (true) {
            display.displayMenu(
                listOf(
                    UiAction("Login",{
                        val user = loginView.getLoggedUser()
                        if (user.userRole == UserRole.ADMIN) {
                            adminDashboardUi(admin = user)
                        }else{
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
