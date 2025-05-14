package org.damascus

import org.damascus.data.mongodb.MongoConnector
import org.damascus.di.appModule
import org.damascus.di.repositoryModule
import org.damascus.di.useCaseModule
import org.damascus.ui.PlanMateConsoleUi
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.getKoin

fun main() {
//    Runtime.getRuntime().addShutdownHook(
//        Thread {
//            MongoConnector.close()
//        }
//    )

    startKoin {
        modules(appModule, repositoryModule, useCaseModule)
    }

    val ui: PlanMateConsoleUi = getKoin().get()
    ui.start()
}