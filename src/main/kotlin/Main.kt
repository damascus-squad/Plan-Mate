package org.damascus

import org.damascus.di.appModule
import org.damascus.ui.PlanMateConsoleUi
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.getKoin

import org.damascus.di.appModule
import org.damascus.di.repositoryModule
import org.damascus.di.useCaseModule
import org.damascus.presentation.PlanMateMoodUi
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.getKoin

fun main() {

    startKoin {
        modules(appModule, repositoryModule, useCaseModule)
    }

    val ui: PlanMateConsoleUi = getKoin().get()
    ui.start()

    val ui: PlanMateMoodUi = getKoin().get()
    ui.start()
}