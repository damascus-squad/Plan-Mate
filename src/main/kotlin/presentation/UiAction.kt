package org.damascus.presentation

data class UiAction(
    val name: String,
    val action: () -> Unit
)
