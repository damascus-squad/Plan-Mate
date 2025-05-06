package org.damascus

import org.damascus.di.appModule
import org.damascus.ui.PlanMateConsoleUi
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.getKoin
import org.damascus.di.repositoryModule
import org.damascus.di.useCaseModule

fun main() {
    startKoin {
        modules(appModule, repositoryModule, useCaseModule)
    }

    val ui: PlanMateConsoleUi = getKoin().get()
    ui.start()
}