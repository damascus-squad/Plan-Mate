package org.damascus.di

import data.csv.helpers.ProjectCsvHelper
import data.csv.helpers.UserCsvHelper
import logic.model.Project
import logic.model.User
import org.damascus.data.authentication.AuthenticationRepoImpl
import org.damascus.data.authentication.MD5HashingService
import org.damascus.data.csv.CsvDataSource
import org.damascus.data.csv.generateCsvHeader
import org.damascus.data.csv.utils.CsvConstants.PROJECTS_FILE
import org.damascus.data.csv.utils.CsvConstants.USERS_FILE
import org.damascus.logic.repository.AuthenticationRepository
import logic.repo.DataSource
import org.damascus.logic.service.HashingService
import org.damascus.logic.usecase.AuthenticationUseCase
import org.damascus.presentation.PlanMateMoodUi
import org.damascus.presentation.io.ConsoleDisplay
import org.damascus.presentation.io.ConsoleUserInput
import org.damascus.presentation.retrieve.PlanRetrieveUi
import org.damascus.ui.io.ConsoleDisplay
import org.damascus.ui.io.ConsoleUserInput
import org.damascus.ui.PlanMateConsoleUi
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

    single<AuthenticationRepository> { AuthenticationRepoImpl(get(), get()) }
    single<HashingService> { MD5HashingService() }
    single { AuthenticationUseCase(get()) }

    // UI
    single { ConsoleUserInput() }
    single { ConsoleDisplay(get()) }
    single { PlanMateConsoleUi(get()) }

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
