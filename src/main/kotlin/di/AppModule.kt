package org.damascus.di

import data.csv.helpers.TaskCsvHelper
import data.csv.helpers.UserCsvHelper
import data.repo.TaskRepositoryImpl
import logic.model.Task
import logic.model.User
import logic.repo.AuditLogRepository
import logic.repo.AuthenticationRepository
import logic.repo.DataSource
import logic.repo.TaskRepository
import logic.repo.TaskStateRepository
import org.damascus.data.repo.AuthenticationRepoImpl
import org.damascus.logic.service.MD5HashingService
import org.damascus.data.csv.CsvDataSource
import org.damascus.data.csv.generateCsvHeader
import org.damascus.data.csv.utils.CsvConstants.TASKS_FILE
import org.damascus.data.csv.utils.CsvConstants.USERS_FILE
import org.damascus.data.repo.TaskStateRepositoryImpl
import org.damascus.logic.service.HashingService
import logic.usecase.auth.CreateMateUseCase
import org.damascus.logic.usecase.auth.AuthenticateUserLoginUseCase
import org.damascus.logic.usecase.task.*
import org.damascus.ui.PlanMateConsoleUi
import org.damascus.ui.io.ConsoleDisplay
import org.damascus.ui.io.ConsoleUserInput
import org.damascus.ui.io.Display
import org.damascus.ui.io.InputReader
import org.damascus.ui.views.LoginView
import org.damascus.ui.views.task.TaskCLI
import org.koin.dsl.module
import org.damascus.data.repo.AuditLogRepositoryImpl

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

    single<DataSource<Task>> {
        CsvDataSource(
            TASKS_FILE,
            { generateCsvHeader<Task>() },
            extractId = { it.id },
            parser = TaskCsvHelper::parseTask,
            serializer = TaskCsvHelper::serializeTask
        )
    }

    single<AuthenticationRepository> { AuthenticationRepoImpl(get(), get()) }
    single<TaskStateRepository> { TaskStateRepositoryImpl(get()) }
    single<TaskRepository> { TaskRepositoryImpl(get()) }
    single<HashingService> { MD5HashingService() }
    single<AuditLogRepository> { AuditLogRepositoryImpl(get()) }

    // Use cases
    single { CreateMateUseCase(get()) }
    single { AuthenticateUserLoginUseCase(get()) }
    single { UpdateTaskUseCase(get()) }
    single { CreateTaskUseCase(get()) }
    single { DeleteTaskUseCase(get()) }
    single { GetTaskUseCase(get()) }
    single { GetTasksByProjectUseCase(get()) }

    // UI
    single<InputReader> { ConsoleUserInput() }
    single<Display> { ConsoleDisplay(get()) }
    single { PlanMateConsoleUi(get()) }
    single { LoginView(get(), get()) }
    single { TaskCLI(get(),get(),get(),get(),get(),get() )}
}