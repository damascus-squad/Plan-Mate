package org.damascus.di

import data.repo.TaskRepositoryImpl
import logic.repo.*
import org.damascus.data.repo.AuditLogsRepositoryImpl
import org.damascus.data.repo.AuthenticationRepoImpl
import org.damascus.data.repo.ProjectRepositoryImpl
import org.damascus.data.repo.TaskStateRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {
    single<ProjectRepository> { ProjectRepositoryImpl(get()) }
    single<AuthenticationRepository> { AuthenticationRepoImpl(get(), get()) }
    single<TaskStateRepository> { TaskStateRepositoryImpl(get()) }
    single<TaskRepository> { TaskRepositoryImpl(get()) }
    single<AuditLogsRepository> { AuditLogsRepositoryImpl(get()) }
}