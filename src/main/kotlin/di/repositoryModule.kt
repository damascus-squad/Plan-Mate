package org.damascus.di

import logic.model.Project
import logic.repo.DataSource
import org.damascus.data.repo.ProjectRepositoryImpl
import org.damascus.logic.repository.ProjectRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<ProjectRepository> {
        ProjectRepositoryImpl(get<DataSource<Project>>())
    }
}
