package org.damascus.di

import logic.model.Project
import org.damascus.data.DataSource
import org.damascus.logic.repository.ProjectRepository
import org.damascus.data.repository.ProjectRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {
    single<ProjectRepository> {
        ProjectRepositoryImpl(get<DataSource<Project>>())
    }
}
