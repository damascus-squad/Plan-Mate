package ui.io

import ui.util.UiAction

interface Display {
    fun displayMenu(
        uiActionList: List<UiAction>,
        menuTitle: String,
        showBackOption: Boolean = true
    )

    fun write(prompt: String)
    fun writeError(errorMessage: String)
}