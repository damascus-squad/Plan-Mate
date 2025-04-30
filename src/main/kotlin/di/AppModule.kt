package org.damascus.di

import org.damascus.data.csv.FileDataParser
import org.damascus.data.csv.FileDataSerializer
import org.damascus.data.csv.FileReader
import org.damascus.utils.Constants
import org.koin.dsl.module
import java.io.File

val appModule = module {
    single { File(Constants.USERS_FILE) }
    single { File(Constants.TASKS_FILE) }
    single { File(Constants.PROJECTS_FILE) }
    single { File(Constants.STATES_FILE) }
    single { File(Constants.HISTORY_FILE) }
    single { FileReader(get()) }
    single { FileDataParser }
    single { FileDataSerializer }

}
