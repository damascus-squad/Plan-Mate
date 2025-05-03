package org.damascus

import org.damascus.di.appModule
import org.koin.core.context.startKoin

import org.damascus.di.appModule
import org.damascus.di.repositoryModule
import org.damascus.di.useCaseModule
import org.damascus.presentation.PlanMateMoodUi
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.getKoin

fun main() {
    startKoin {
        modules(appModule)
    }
    startKoin {
        modules(appModule, repositoryModule, useCaseModule)
    }

    val ui: PlanMateMoodUi = getKoin().get()
    ui.start()
}