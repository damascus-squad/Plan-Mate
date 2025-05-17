package org.damascus.ui.util

data class UiAction(
    val name: String,
    val action:suspend () -> Unit,
    val refreshAction:suspend () -> Unit = {},
    val exitAfterAction: Boolean = false
)