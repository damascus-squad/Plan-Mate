package org.damascus.di

import logic.model.Project
import logic.repo.DataSource
import logic.repo.ProjectRepository
import org.damascus.data.repo.ProjectRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {
    single<ProjectRepository> {
        ProjectRepositoryImpl(get<DataSource<Project>>())
    }
}
