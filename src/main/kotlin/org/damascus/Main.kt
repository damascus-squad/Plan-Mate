package org.damascus

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.damascus.di.appModule
import org.damascus.di.repositoryModule
import org.damascus.di.useCaseModule
import org.damascus.ui.PlanMateConsoleUi
import org.damascus.ui.util.showEmojiLoading
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.getKoin

 fun main() {
     val appScope = CoroutineScope(Dispatchers.Default)
     startKoin {
        modules(appModule, repositoryModule, useCaseModule)
    }
     appScope.launch {
         val loadingJob = launch {
             showEmojiLoading("Starting PlanMate")
         }
         delay(1000)
         val ui: PlanMateConsoleUi = getKoin().get()
         loadingJob.cancelAndJoin()
         println("\r")
         ui.start()
     }
     Thread.sleep(50_000)
 }