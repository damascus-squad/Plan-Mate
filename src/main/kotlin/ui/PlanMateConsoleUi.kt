package org.damascus.ui

import org.damascus.ui.io.ConsoleDisplay

class PlanMateConsoleUi(
    private val consoleUserDisplay: ConsoleDisplay
) {

    fun start() {
        consoleUserDisplay.displayMenu(
            uiActionList = emptyList(),
            menuTitle = MENU_TITLE
        )
    }

    private companion object {
        const val MENU_TITLE = "Welcome to The Plan Mate App"
    }
}
