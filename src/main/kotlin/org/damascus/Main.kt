package org.damascus

import org.damascus.annotation.KoverIgnore
import org.damascus.di.KoinAppModule
import org.damascus.di.dataSourceModule
import kotlinx.coroutines.*
import org.damascus.di.appModule
import org.damascus.di.repositoryModule
import org.damascus.di.useCaseModule
import org.damascus.ui.PlanMateConsoleUi
import org.koin.core.context.GlobalContext
import org.damascus.ui.util.showEmojiLoading
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module

@KoverIgnore("Main function")
fun main() {
    startKoin {
        modules(
            KoinAppModule().module,
            dataSourceModule
        )
    }
suspend fun main() = coroutineScope {
    initializeKoin()

    val loading = launchLoadingAnimation("Starting Plan-Mate")

    delay(1000)

    val ui: PlanMateConsoleUi = GlobalContext.get().get<PlanMateConsoleUi>()
    val ui: PlanMateConsoleUi = getKoin().get()

    loading.cancelAndJoin()

    println()

    ui.start()
}

private fun initializeKoin() {
    startKoin {
        modules(appModule, repositoryModule, useCaseModule)
    }
}

private fun CoroutineScope.launchLoadingAnimation(message: String): Job {
    return launch {
        showEmojiLoading(message)
    }
}
