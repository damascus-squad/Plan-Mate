package org.damascus

import org.damascus.annotation.KoverIgnore
import org.damascus.di.KoinAppModule
import org.damascus.di.dataSourceModule
import org.damascus.ui.PlanMateConsoleUi
import org.koin.core.context.GlobalContext
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

    val ui: PlanMateConsoleUi = GlobalContext.get().get<PlanMateConsoleUi>()
    ui.start()
}