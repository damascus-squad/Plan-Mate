package org.damascus.ui.util

data class UiAction(
    val name: String,
    val action: () -> Unit
)