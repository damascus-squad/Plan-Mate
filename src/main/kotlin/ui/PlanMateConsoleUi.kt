package org.damascus.ui

import org.damascus.ui.io.ConsoleDisplay
import org.damascus.ui.retrieve.PlanRetrieve
import org.damascus.ui.util.UiAction
import org.damascus.ui.views.LoginView

class PlanMateConsoleUi(
    private val consoleUserDisplay: ConsoleDisplay,
    private val planRetrieve: PlanRetrieve

    private val loginView: LoginView
) {

    fun start() {
        while (true) {
            val user = loginView.getLoggedUser()
        }
        consoleUserDisplay.displayMenu(
            uiActionList = listOf(
                UiAction("📁 Projects") {
                    consoleUserDisplay.displayMenu(
                        uiActionList = listOf(
                            UiAction("🔧 Create New Project") { planRetrieve.createProject() },
                            UiAction("📋 Show All Projects") { planRetrieve.displayProjects() },
                            UiAction("⚙️ Manage Existing Project") { planRetrieve.manageProject() }
                        ),
                        menuTitle = "🗂️ Projects Menu"
                    )
                }
            ),
            menuTitle = MENU_TITLE
        )
    }


    private companion object {
        const val MENU_TITLE = "Welcome to The Plan Mate App"
    }
}
