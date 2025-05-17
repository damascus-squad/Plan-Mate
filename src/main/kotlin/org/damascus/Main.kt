package org.damascus

import kotlinx.coroutines.*
import org.damascus.annotation.KoverIgnore
import org.damascus.data.mongodb.MongoConnector
import org.damascus.di.KoinAppModule
import org.damascus.di.dataSourceModule
import org.damascus.ui.PlanMateConsoleUi
import org.damascus.ui.util.showEmojiLoading
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module

@KoverIgnore("Main function")
suspend fun main() = coroutineScope {

    Runtime.getRuntime().addShutdownHook(
        Thread {
            MongoConnector.close()
        }
    )

    initializeKoin()

    val loading = launchLoadingAnimation("Starting PlanMate")

    delay(1000)

    val ui: PlanMateConsoleUi = GlobalContext.get().get<PlanMateConsoleUi>()

    loading.cancelAndJoin()

    println()

    ui.start()
}

private fun initializeKoin() {
    startKoin {
        modules(
            KoinAppModule().module,
            dataSourceModule
        )
    }
}

private fun CoroutineScope.launchLoadingAnimation(message: String): Job = launch {
    showEmojiLoading(message)
}
