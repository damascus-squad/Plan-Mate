package ui

import ui.views.LoginView

class PlanMateConsoleUi(
    private val loginView: LoginView
) {

    fun start() {
        while (true) {
            val user = loginView.getLoggedUser()
        }
    }


    private companion object {
        const val MENU_TITLE = "Welcome to The Plan Mate App"
    }
}
