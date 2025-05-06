package di

import data.repo.*
import logic.repo.*
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