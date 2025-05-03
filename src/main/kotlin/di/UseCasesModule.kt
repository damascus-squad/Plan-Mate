package org.damascus.di

import org.damascus.logic.usecase.project.CreateProjectUseCase
import org.damascus.logic.usecase.project.DeleteProjectUseCase
import org.damascus.logic.usecase.project.GetAllProjectsByMateIdUseCase
import org.damascus.logic.usecase.project.GetAllProjectsUseCase
import org.damascus.logic.usecase.project.GetProjectUseCase
import org.damascus.logic.usecase.project.ModifyMateAssignmentUseCase
import org.damascus.logic.usecase.project.UpdateProjectUseCase
import org.koin.dsl.module

val useCaseModule = module {
    single { CreateProjectUseCase(get()) }
    single { GetAllProjectsUseCase(get()) }
    single { DeleteProjectUseCase(get()) }
    single { UpdateProjectUseCase(get()) }
    single { GetAllProjectsByMateIdUseCase(get()) }
    single { GetProjectUseCase(get()) }
    single { ModifyMateAssignmentUseCase(get()) }
}