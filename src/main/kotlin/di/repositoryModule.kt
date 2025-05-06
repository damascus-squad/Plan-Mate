package di

import data.repo.*
import logic.repo.*
import org.koin.dsl.module

val repositoryModule = module {
    single<ProjectRepository> { ProjectRepositoryImpl(get()) }
    single<AuthenticationRepository> { AuthenticationRepoImpl(get(), get()) }
    single<TaskStateRepository> { TaskStateRepositoryImpl(get()) }
    single<TaskRepository> { TaskRepositoryImpl(get()) }
    single<AuditLogsRepository> { AuditLogsRepositoryImpl(get()) }
}