package org.damascus.di

import org.damascus.logic.usecase.ProjectUseCase.CreateProjectUseCase
import org.damascus.logic.usecase.ProjectUseCase.DeleteProjectUseCase
import org.damascus.logic.usecase.ProjectUseCase.GetAllProjectsByMateIdUseCase
import org.damascus.logic.usecase.ProjectUseCase.GetAllProjectsUseCase
import org.damascus.logic.usecase.ProjectUseCase.GetProjectUseCase
import org.damascus.logic.usecase.ProjectUseCase.ModifyMateAssignmentUseCase
import org.damascus.logic.usecase.ProjectUseCase.UpdateProjectUseCase
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