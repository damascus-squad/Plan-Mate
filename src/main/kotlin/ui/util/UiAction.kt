package ui.util

data class UiAction(
    val name: String,
    val action: () -> Unit,
    val refreshAction: () -> Unit = {},
    val exitAfterAction: Boolean = false
)