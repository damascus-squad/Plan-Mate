package org.damascus.di

import logic.usecase.auth.*
import logic.usecase.project.*
import org.damascus.logic.usecase.project.GetProjectStateUseCase
import org.damascus.logic.usecase.task.*
import org.koin.dsl.module

val useCaseModule = module {
    // Project use cases
    single { CreateProjectUseCase(get()) }
    single { GetAllProjectsUseCase(get()) }
    single { DeleteProjectUseCase(get()) }
    single { UpdateProjectUseCase(get()) }
    single { GetAllProjectsByMateIdUseCase(get()) }
    single { GetProjectUseCase(get()) }
    single { ModifyMateAssignmentUseCase(get()) }
    single { GetProjectStateUseCase(get(), get()) }

    // Authentication use cases
    single { CreateMateUseCase(get()) }
    single { AuthenticateUserLoginUseCase(get()) }

    // Task use cases
    single { CreateTaskUseCase(get()) }
    single { UpdateTaskUseCase(get()) }
    single { DeleteTaskUseCase(get()) }
    single { GetTaskUseCase(get()) }
    single { GetTasksByProjectUseCase(get()) }
}