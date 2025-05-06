package di

import data.csv.CsvDataSource
import data.csv.generateCsvHeader
import data.csv.helpers.ProjectCsvHelper
import data.csv.helpers.TaskCsvHelper
import data.csv.helpers.UserCsvHelper
import data.csv.utils.CsvConstants.PROJECTS_FILE
import data.csv.utils.CsvConstants.TASKS_FILE
import data.csv.utils.CsvConstants.USERS_FILE
import data.dto.UserDTO
import logic.model.Project
import logic.model.Task
import logic.repo.DataSource
import logic.service.HashingService
import logic.service.MD5HashingService
import org.koin.dsl.module
import ui.PlanMateConsoleUi
import ui.io.ConsoleDisplay
import ui.io.ConsoleUserInput
import ui.io.Display
import ui.io.InputReader
import ui.views.LoginView
import ui.views.project.ProjectView
import ui.views.project.ProjectViewCli
import ui.views.task.TaskCLI

val appModule = module {

    single<DataSource<UserDTO>> {
        CsvDataSource(
            USERS_FILE,
            { generateCsvHeader<UserDTO>() },
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