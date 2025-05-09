package di

import data.csv.CsvDataSource
import data.csv.generateCsvHeader
import data.csv.helpers.*
import data.csv.utils.CsvConstants.HISTORY_FILE
import data.csv.utils.CsvConstants.PROJECTS_FILE
import data.csv.utils.CsvConstants.TASKS_FILE
import data.csv.utils.CsvConstants.TASK_STATES_FILE
import data.csv.utils.CsvConstants.USERS_FILE
import data.dto.UserDTO
import logic.model.History
import logic.model.Project
import logic.model.Task
import logic.model.TaskState
import logic.repo.DataSource
import logic.service.HashingService
import logic.service.MD5HashingService
import org.damascus.ui.views.admin.AdminDashboardUi
import org.damascus.ui.views.admin.MateDashboardUi
import org.damascus.ui.views.admin.SelectMateUi
import org.damascus.ui.views.auditLog.ProjectLogUi
import org.damascus.ui.views.auditLog.TaskLogUi
import org.damascus.ui.views.project.*
import org.koin.core.qualifier.named
import org.koin.dsl.module
import ui.PlanMateConsoleUi
import ui.io.ConsoleDisplay
import ui.io.ConsoleUserInput
import ui.io.Display
import ui.io.InputReader
import org.damascus.ui.views.projectDashboard.ProjectDashboardCli
import org.damascus.ui.views.task.CreateTaskUi
import org.damascus.ui.views.task.GetAllTasksByProjectIdUi
import ui.views.LoginView
import ui.views.project.SelectProjectUi
import ui.views.task.TaskCLI

val appModule = module {

    single<DataSource<UserDTO>>(qualifier = named("userDataSource")) {
        CsvDataSource(
            USERS_FILE,
            { generateCsvHeader<UserDTO>() },
            extractId = { it.id },
            parser = UserCsvHelper::parseUser,
            serializer = UserCsvHelper::serializeUser
        )
    }

    single<DataSource<Project>>(qualifier = named("projectDataSource")) {
        CsvDataSource(
            PROJECTS_FILE,
            { generateCsvHeader<Project>() },
            extractId = { it.id },
            parser = ProjectCsvHelper::parseProject,
            serializer = ProjectCsvHelper::serializeProject
        )
    }

    single<DataSource<Task>>(qualifier = named("taskDataSource")) {
        CsvDataSource(
            TASKS_FILE,
            { generateCsvHeader<Task>() },
            extractId = { it.id },
            parser = TaskCsvHelper::parseTask,
            serializer = TaskCsvHelper::serializeTask
        )
    }

    single<DataSource<History>>(qualifier = named("historyDataSource")) {
        CsvDataSource(
            HISTORY_FILE,
            { generateCsvHeader<History>() },
            extractId = { it.id },
            parser = HistoryCsvHelper::parseHistory,
            serializer = HistoryCsvHelper::serializeHistory
        )
    }

    single<DataSource<TaskState>>(qualifier = named("taskStateDataSource")) {
        CsvDataSource(
            TASK_STATES_FILE,
            { generateCsvHeader<TaskState>() },
            extractId = { it.id },
            parser = TaskStateCsvHelper::parseTaskState,
            serializer = TaskStateCsvHelper::serializeTaskState
        )
    }

    single<HashingService> { MD5HashingService() }

    single<Display> { ConsoleDisplay(get()) }
    single<InputReader> { ConsoleUserInput() }

    single { AdminDashboardUi(get(), get(), get()) }
    single { MateDashboardUi(get()) }
    single { SelectMateUi(get(), get()) }
    single { AssignMateToProjectUi(get(), get(), get(), get()) }
    single { DeleteProjectUi(get(), get(), get(), get()) }
    single { UpdateProjectUi(get(), get(), get(), get(), get(), get()) }
    single { UpdateProjectTitleUi(get(), get(), get(), get()) }
    single { GetAdminProjectsUi(get(), get()) }
    single { GetMateProjectsUi(get(), get()) }
    single { SelectProjectUi(get(), get(), get()) }
    single { ProjectUi(get(), get(), get(), get(), get(), get(), get(), get()) }
    single { UnAssignMateFromProjectUi(get(), get(), get(), get()) }
    single { LoginView(get(), get(), get()) }
    single { CreateTaskUi(get(), get(),get(), get(),get()) }
    single { TaskCLI(get(), get(), get(), get(), get(), get()) }
    single { PlanMateConsoleUi(get(), get(), get()) }
    single { CreateProjectUi(get(), get(), get()) }
    single { ProjectDashboardCli(get(), get(), get(), get(), get(), get()) }
    single { ProjectLogUi(get(), get(), get()) }
    single { TaskLogUi(get(), get(), get()) }
    single { GetAllTasksByProjectIdUi(get()) }
}