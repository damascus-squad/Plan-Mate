package ui

import logic.model.UserRole
import org.damascus.ui.views.admin.AdminDashboardView
import org.damascus.ui.views.projectDashboard.ProjectDashboardCli
import org.damascus.ui.views.projectDashboard.ProjectDashboardController
import ui.views.LoginView
import ui.views.project.ProjectView
import ui.views.project.ProjectViewCli

class PlanMateConsoleUi(
    private val loginView: LoginView,
    private val adminDashboardView: AdminDashboardView,
    private val projectViewCli: ProjectView
) {

    fun start() {
        while (true) {
            val user = loginView.getLoggedUser()
            if (user.userRole == UserRole.ADMIN) {
                adminDashboardView.showDashboard(user)
            }else{
                projectViewCli.showAllProjects(currentUser = user)
            }
        }
    }


    private companion object {
        const val MENU_TITLE = "Welcome to The Plan Mate App"
    }
}
