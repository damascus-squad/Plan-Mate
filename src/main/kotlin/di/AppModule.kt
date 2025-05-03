package org.damascus.di

import data.csv.helpers.ProjectCsvHelper
import data.csv.helpers.UserCsvHelper
import logic.model.Project
import logic.model.User
import org.damascus.data.DataSource
import org.damascus.data.csv.CsvDataSource
import org.damascus.data.csv.generateCsvHeader
import org.damascus.data.csv.utils.CsvConstants.PROJECTS_FILE
import org.damascus.data.csv.utils.CsvConstants.USERS_FILE
import org.damascus.presentation.PlanMateMoodUi
import org.damascus.presentation.io.ConsoleDisplay
import org.damascus.presentation.io.ConsoleUserInput
import org.damascus.presentation.retrieve.PlanRetrieveUi
import org.koin.dsl.module

val appModule = module {

    single<DataSource<User>> {
        CsvDataSource(
            USERS_FILE,
            { generateCsvHeader<User>() },
            extractId = { it.id },
            parser = UserCsvHelper::parseUser,
            serializer = UserCsvHelper::serializeUser
        )
    }

    single<DataSource<Project>> {
        CsvDataSource(
            PROJECTS_FILE,
            { generateCsvHeader<Project>() },
            extractId = { it.id },
            parser = ProjectCsvHelper::parseProject,
            serializer = ProjectCsvHelper::serializeProject
        )
    }

    single { ConsoleUserInput() }
    single { ConsoleDisplay() }

    single {
        PlanRetrieveUi(
            consoleDisplay = get(),
            consoleUserInput = get(),
            createProjectUseCase = get(),
            getAllProjectsUseCase = get(),
            deleteProjectUseCase = get(),
            updateProjectUseCase = get()
        )
    }

    single {
        PlanMateMoodUi(
            get(),
            get()
        )
    }
}
