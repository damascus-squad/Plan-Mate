package org.damascus.di

import kotlinx.coroutines.Dispatchers
import org.damascus.data.dto.*
import org.damascus.data.mongodb.MongoConnector
import org.damascus.data.mongodb.MongoConstants.DATABASE_NAME
import org.damascus.data.mongodb.MongoConstants.HISTORY_COLLECTION
import org.damascus.data.mongodb.MongoConstants.PROJECTS_COLLECTION
import org.damascus.data.mongodb.MongoConstants.TASKS_COLLECTION
import org.damascus.data.mongodb.MongoConstants.TASK_STATES_COLLECTION
import org.damascus.data.mongodb.MongoConstants.USERS_COLLECTION
import org.damascus.data.mongodb.MongoDataSource
import org.damascus.logic.repo.DataSource
import org.koin.core.qualifier.named
import org.koin.dsl.module

val dataSourceModule = module {
    single<DataSource<UserDTO>>(named("userDataSource")) {
        MongoDataSource(
            mongoDatabase = MongoConnector.getDatabase(DATABASE_NAME),
            collectionName = USERS_COLLECTION,
            documentClass = UserDTO::class.java,
            dispatcher = Dispatchers.IO
        )
        /*
        CsvDataSource(
            CsvConstants.USERS_FILE,
            { generateCsvHeader<UserDTO>() },
            extractId = { it.id },
            parser = UserCsvHelper::parseUser,
            serializer = UserCsvHelper::serializeUser
        )
        */
    }

    single<DataSource<ProjectDTO>>(named("projectDataSource")) {
        MongoDataSource(
            mongoDatabase = MongoConnector.getDatabase(DATABASE_NAME),
            collectionName = PROJECTS_COLLECTION,
            documentClass = ProjectDTO::class.java,
            dispatcher = Dispatchers.IO
        )
/*        CsvDataSource(
            CsvConstants.PROJECTS_FILE,
            { generateCsvHeader<Project>() },
            extractId = { it.id },
            parser = ProjectCsvHelper::parseProject,
            serializer = ProjectCsvHelper::serializeProject
        )*/
    }

    single<DataSource<TaskDTO>>(named("taskDataSource")) {
        MongoDataSource(
            mongoDatabase = MongoConnector.getDatabase(DATABASE_NAME),
            collectionName = TASKS_COLLECTION,
            documentClass = TaskDTO::class.java,
            dispatcher = Dispatchers.IO
        )
/*        CsvDataSource(
            CsvConstants.TASKS_FILE,
            { generateCsvHeader<Task>() },
            extractId = { it.id },
            parser = TaskCsvHelper::parseTask,
            serializer = TaskCsvHelper::serializeTask
        )*/
    }

    single<DataSource<HistoryLogDTO>>(named("historyDataSource")) {
        MongoDataSource(
            mongoDatabase = MongoConnector.getDatabase(DATABASE_NAME),
            collectionName = HISTORY_COLLECTION,
            documentClass = HistoryLogDTO::class.java,
            dispatcher = Dispatchers.IO
        )
/*        CsvDataSource(
            CsvConstants.HISTORY_FILE,
            { generateCsvHeader<History>() },
            extractId = { it.id },
            parser = HistoryCsvHelper::parseHistory,
            serializer = HistoryCsvHelper::serializeHistory
        )*/
    }

    single<DataSource<TaskStateDTO>>(named("taskStateDataSource")) {
        MongoDataSource(
            mongoDatabase = MongoConnector.getDatabase(DATABASE_NAME),
            collectionName = TASK_STATES_COLLECTION,
            documentClass = TaskStateDTO::class.java,
            dispatcher = Dispatchers.IO
        )
/*        CsvDataSource(
            CsvConstants.TASK_STATES_FILE,
            { generateCsvHeader<TaskState>() },
            extractId = { it.id },
            parser = TaskStateCsvHelper::parseTaskState,
            serializer = TaskStateCsvHelper::serializeTaskState
        )*/
    }
}