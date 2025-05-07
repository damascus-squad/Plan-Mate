package org.damascus

import di.appModule
import di.repositoryModule
import di.useCaseModule
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.getKoin
import ui.PlanMateConsoleUi

fun main() {
    startKoin {
        modules(appModule, repositoryModule, useCaseModule)
    }

    val ui: PlanMateConsoleUi = getKoin().get()
    ui.start()
}