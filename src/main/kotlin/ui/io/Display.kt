package ui.io

import ui.util.UiAction

interface Display {
    fun displayMenu(uiActionList: List<UiAction>, menuTitle: String)
}