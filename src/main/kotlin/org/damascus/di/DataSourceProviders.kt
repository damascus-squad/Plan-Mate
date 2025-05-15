package org.damascus.di

import org.damascus.data.csv.CsvDataSource
import org.damascus.data.csv.generateCsvHeader
import org.damascus.data.csv.helpers.*
import org.damascus.data.csv.utils.CsvConstants
import org.damascus.data.dto.UserDTO
import org.damascus.logic.model.History
import org.damascus.logic.model.Project
import org.damascus.logic.model.Task
import org.damascus.logic.model.TaskState
import org.damascus.logic.repo.DataSource
import org.koin.core.qualifier.named
import org.koin.dsl.module

val dataSourceModule = module {
    single<DataSource<UserDTO>>(named("userDataSource")) {
        CsvDataSource(
            CsvConstants.USERS_FILE,
            { generateCsvHeader<UserDTO>() },
            extractId = { it.id },
            parser = UserCsvHelper::parseUser,
            serializer = UserCsvHelper::serializeUser
        )
    }

    single<DataSource<Project>>(named("projectDataSource")) {
        CsvDataSource(
            CsvConstants.PROJECTS_FILE,
            { generateCsvHeader<Project>() },
            extractId = { it.id },
            parser = ProjectCsvHelper::parseProject,
            serializer = ProjectCsvHelper::serializeProject
        )
    }

    single<DataSource<Task>>(named("taskDataSource")) {
        CsvDataSource(
            CsvConstants.TASKS_FILE,
            { generateCsvHeader<Task>() },
            extractId = { it.id },
            parser = TaskCsvHelper::parseTask,
            serializer = TaskCsvHelper::serializeTask
        )
    }

    single<DataSource<History>>(named("historyDataSource")) {
        CsvDataSource(
            CsvConstants.HISTORY_FILE,
            { generateCsvHeader<History>() },
            extractId = { it.id },
            parser = HistoryCsvHelper::parseHistory,
            serializer = HistoryCsvHelper::serializeHistory
        )
    }

    single<DataSource<TaskState>>(named("taskStateDataSource")) {
        CsvDataSource(
            CsvConstants.TASK_STATES_FILE,
            { generateCsvHeader<TaskState>() },
            extractId = { it.id },
            parser = TaskStateCsvHelper::parseTaskState,
            serializer = TaskStateCsvHelper::serializeTaskState
        )
    }
}