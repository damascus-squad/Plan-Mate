package org.damascus.di

import org.damascus.data.csv.CsvDataSource
import org.damascus.data.csv.generateCsvHeader
import org.damascus.data.dto.UserDTO
import org.damascus.logic.model.History
import org.damascus.logic.model.Project
import org.damascus.logic.model.Task
import org.damascus.logic.model.TaskState
import org.damascus.logic.repo.DataSource
import org.damascus.logic.service.HashingService
import org.damascus.logic.service.MD5HashingService
import org.damascus.data.csv.helpers.HistoryCsvHelper
import org.damascus.data.csv.helpers.ProjectCsvHelper
import org.damascus.data.csv.helpers.TaskCsvHelper
import org.damascus.data.csv.helpers.TaskStateCsvHelper
import org.damascus.data.csv.helpers.UserCsvHelper
import org.damascus.data.csv.utils.CsvConstants
import org.damascus.ui.PlanMateConsoleUi
import org.damascus.ui.io.ConsoleDisplay
import org.damascus.ui.io.ConsoleUserInput
import org.damascus.ui.io.Display
import org.damascus.ui.io.InputReader
import org.damascus.ui.views.user.LoginView
import org.damascus.ui.views.auditLog.ProjectLogUi
import org.damascus.ui.views.auditLog.TaskLogUi
import org.damascus.ui.views.project.AllProjectsUi
import org.damascus.ui.views.project.AssignMateToProjectUi
import org.damascus.ui.views.project.CreateProjectUi
import org.damascus.ui.views.project.DeleteProjectUi
import org.damascus.ui.views.project.GetAdminProjectsUi
import org.damascus.ui.views.project.GetMateProjectsUi
import org.damascus.ui.views.project.ProjectManagementUi
import org.damascus.ui.views.project.SelectProjectUi
import org.damascus.ui.views.project.UnAssignMateFromProjectUi
import org.damascus.ui.views.project.UpdateProjectTitleUi
import org.damascus.ui.views.project.UpdateProjectUi
import org.damascus.ui.views.task.CreateTaskUi
import org.damascus.ui.views.user.AdminDashboardUi
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.damascus.ui.views.task.DeleteTaskUi
import org.damascus.ui.views.task.GetAllTasksByProjectIdUi
import org.damascus.ui.views.task.SelectTaskUi
import org.damascus.ui.views.task.TaskDashboardUi
import org.damascus.ui.views.task.TaskMainUi
import org.damascus.ui.views.task.UpdateTaskAssigneeUi
import org.damascus.ui.views.task.UpdateTaskDescriptionUi
import org.damascus.ui.views.task.UpdateTaskStatusUi
import org.damascus.ui.views.task.UpdateTaskTitleUi
import org.damascus.ui.views.task.UpdateTaskUi
import org.damascus.ui.views.user.CreateMateUi
import org.damascus.ui.views.user.GetAllMatesUi
import org.damascus.ui.views.user.MateDashboardUi
import org.damascus.ui.views.user.MateManagementUi
import org.damascus.ui.views.user.SelectMateUi
import org.damascus.ui.views.taskState.TaskStateDashboard

val appModule = module {

    single<DataSource<UserDTO>>(qualifier = named("userDataSource")) {
        CsvDataSource(
            CsvConstants.USERS_FILE,
            { generateCsvHeader<UserDTO>() },
            extractId = { it.id },
            parser = UserCsvHelper::parseUser,
            serializer = UserCsvHelper::serializeUser
        )
    }

    single<DataSource<Project>>(qualifier = named("projectDataSource")) {
        CsvDataSource(
            CsvConstants.PROJECTS_FILE,
            { generateCsvHeader<Project>() },
            extractId = { it.id },
            parser = ProjectCsvHelper::parseProject,
            serializer = ProjectCsvHelper::serializeProject
        )
    }

    single<DataSource<Task>>(qualifier = named("taskDataSource")) {
        CsvDataSource(
            CsvConstants.TASKS_FILE,
            { generateCsvHeader<Task>() },
            extractId = { it.id },
            parser = TaskCsvHelper::parseTask,
            serializer = TaskCsvHelper::serializeTask
        )
    }

    single<DataSource<History>>(qualifier = named("historyDataSource")) {
        CsvDataSource(
            CsvConstants.HISTORY_FILE,
            { generateCsvHeader<History>() },
            extractId = { it.id },
            parser = HistoryCsvHelper::parseHistory,
            serializer = HistoryCsvHelper::serializeHistory
        )
    }

    single<DataSource<TaskState>>(qualifier = named("taskStateDataSource")) {
        CsvDataSource(
            CsvConstants.TASK_STATES_FILE,
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
    single { MateDashboardUi(get(), get(), get(), get(), get()) }
    single { SelectMateUi(get(), get()) }
    single { AssignMateToProjectUi(get(), get(), get(), get()) }
    single { DeleteProjectUi(get(), get(), get(), get()) }
    single { UpdateProjectUi(get(), get(), get(), get(), get(), get()) }
    single { UpdateProjectTitleUi(get(), get(), get(), get()) }
    single { GetAdminProjectsUi(get(), get()) }
    single { GetMateProjectsUi(get(), get()) }
    single { SelectProjectUi(get(), get(), get()) }
    single { AllProjectsUi(get(), get(), get(), get(), get(), get()) }
    single { UnAssignMateFromProjectUi(get(), get(), get(), get()) }
    single { CreateTaskUi(get(), get(), get(), get(), get(), get()) }
    single { PlanMateConsoleUi(get(), get(), get(), get()) }
    single { CreateProjectUi(get(), get(), get(), get()) }
    single { ProjectManagementUi(get(), get(), get(), get(), get(), get()) }
    single { ProjectLogUi(get(), get(), get(), get()) }
    single { TaskLogUi(get(), get(), get(), get()) }
    single { GetAllTasksByProjectIdUi(get(), get(), get()) }
    single { UpdateProjectUi(get(), get(), get(), get(), get(), get()) }
    single { TaskDashboardUi(get(), get(), get(), get(), get(), get(), get()) }
    single { LoginView(get(), get(), get()) }
    single { CreateMateUi(get()) }
    single { GetAllMatesUi(get(), get()) }
    single { MateManagementUi(get(), get(), get()) }

    single { TaskMainUi(get(), get(), get(), get(), get(), get()) }
    single { UpdateTaskUi(get(), get(), get(), get(), get(), get()) }
    single { SelectTaskUi(get(), get(), get()) }
    single { UpdateTaskStatusUi(get(), get(), get(), get(), get(), get()) }
    single { UpdateTaskTitleUi(get(), get(), get(), get(), get(), get()) }
    single { UpdateTaskDescriptionUi(get(), get(), get(), get(), get(), get()) }
    single { UpdateTaskAssigneeUi(get(), get(), get(), get(), get(), get()) }
    single { DeleteTaskUi(get(), get(), get(), get()) }
    single { TaskStateDashboard(get(), get(), get(), get(), get()) }
}