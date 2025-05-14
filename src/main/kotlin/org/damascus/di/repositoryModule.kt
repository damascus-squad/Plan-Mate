package org.damascus.di

import org.damascus.data.repo.AuditLogsRepositoryImpl
import org.damascus.data.repo.AuthenticationRepoImpl
import org.damascus.data.repo.ProjectRepositoryImpl
import org.damascus.data.repo.TaskRepositoryImpl
import org.damascus.data.repo.TaskStateRepositoryImpl
import org.damascus.logic.repo.AuditLogsRepository
import org.damascus.logic.repo.AuthenticationRepository
import org.damascus.logic.repo.ProjectRepository
import org.damascus.logic.repo.TaskRepository
import org.damascus.logic.repo.TaskStateRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module

val repositoryModule = module {
    single<ProjectRepository> {
        ProjectRepositoryImpl(
            get(qualifier = named("projectDataSource"))
        )
    }

    single<AuthenticationRepository> {
        AuthenticationRepoImpl(
            get(),
            get(qualifier = named("userDataSource"))
        )
    }

    single<TaskStateRepository> {
        TaskStateRepositoryImpl(
            get(qualifier = named("taskStateDataSource"))
        )
    }

    single<TaskRepository> {
        TaskRepositoryImpl(
            get(qualifier = named("taskDataSource"))
        )
    }

    single<AuditLogsRepository> {
        AuditLogsRepositoryImpl(
            get(qualifier = named("historyDataSource"))
        )
    }
}