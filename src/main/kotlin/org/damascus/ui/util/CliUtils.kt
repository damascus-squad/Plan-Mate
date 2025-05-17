package org.damascus.ui.util

import kotlinx.coroutines.*
import kotlinx.coroutines.NonCancellable.isActive

suspend fun showEmojiLoading(message: String) {
        val frames = listOf("📅", "📝", "📋", "✅")
        var i = 0
        while (isActive) {
            print("\r$message ${frames[i % frames.size]}  ")
            delay(300)
            i++
        }
    }
