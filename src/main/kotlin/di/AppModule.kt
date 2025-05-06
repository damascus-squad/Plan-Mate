package org.damascus.di

import data.csv.helpers.ProjectCsvHelper
import data.csv.helpers.TaskCsvHelper
import data.csv.helpers.UserCsvHelper
import logic.model.Project
import logic.model.Task
import logic.model.User
import logic.repo.DataSource
import org.damascus.data.csv.CsvDataSource
import org.damascus.data.csv.generateCsvHeader
import org.damascus.data.csv.utils.CsvConstants.PROJECTS_FILE
import org.damascus.data.csv.utils.CsvConstants.TASKS_FILE
import org.damascus.data.csv.utils.CsvConstants.USERS_FILE
import org.damascus.logic.service.HashingService
import org.damascus.logic.service.MD5HashingService
import org.damascus.ui.PlanMateConsoleUi
import org.damascus.ui.io.ConsoleDisplay
import org.damascus.ui.io.ConsoleUserInput
import org.damascus.ui.io.Display
import org.damascus.ui.io.InputReader
import org.damascus.ui.views.LoginView
import org.damascus.ui.views.project.ProjectView
import org.damascus.ui.views.project.ProjectViewCli
import org.damascus.ui.views.task.TaskCLI
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

    single<DataSource<Task>> {
        CsvDataSource(
            TASKS_FILE,
            { generateCsvHeader<Task>() },
            extractId = { it.id },
            parser = TaskCsvHelper::parseTask,
            serializer = TaskCsvHelper::serializeTask
        )
    }

    single<HashingService> { MD5HashingService() }

    single<Display> { ConsoleDisplay(get()) }
    single<InputReader> { ConsoleUserInput() }

    single<ProjectView> { ProjectViewCli(get(), get(), get(), get(), get()) }
    single { LoginView(get(), get()) }
    single { TaskCLI(get(), get(), get(), get(), get(), get()) }
    single { PlanMateConsoleUi(get()) }
}