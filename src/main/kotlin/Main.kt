package org.damascus

import org.damascus.di.appModule
import org.damascus.ui.PlanMateConsoleUi
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.getKoin

fun main() {
    startKoin {
        modules(appModule)
    }

    val ui: PlanMateConsoleUi = getKoin().get()
    ui.start()
}