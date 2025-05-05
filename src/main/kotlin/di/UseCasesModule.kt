package org.damascus.di

import logic.usecase.auth.AuthenticateUserLoginUseCase
import logic.usecase.auth.CreateMateUseCase
import logic.usecase.project.ModifyMateAssignmentUseCase
import org.damascus.logic.usecase.ProjectUseCase.CreateProjectUseCase
import org.damascus.logic.usecase.ProjectUseCase.DeleteProjectUseCase
import org.damascus.logic.usecase.ProjectUseCase.GetAllProjectsByMateIdUseCase
import org.damascus.logic.usecase.ProjectUseCase.GetAllProjectsUseCase
import org.damascus.logic.usecase.ProjectUseCase.GetProjectUseCase
import org.damascus.logic.usecase.ProjectUseCase.UpdateProjectUseCase
import org.damascus.logic.usecase.task.CreateTaskUseCase
import org.damascus.logic.usecase.task.DeleteTaskUseCase
import org.damascus.logic.usecase.task.GetTaskUseCase
import org.damascus.logic.usecase.task.GetTasksByProjectUseCase
import org.damascus.logic.usecase.task.UpdateTaskUseCase
import org.koin.dsl.module

val useCaseModule = module {
    single { CreateProjectUseCase(get()) }
    single { GetAllProjectsUseCase(get()) }
    single { DeleteProjectUseCase(get()) }
    single { UpdateProjectUseCase(get()) }
    single { GetAllProjectsByMateIdUseCase(get()) }
    single { GetProjectUseCase(get()) }
    single { ModifyMateAssignmentUseCase(get()) }

    single { CreateMateUseCase(get()) }
    single { AuthenticateUserLoginUseCase(get()) }
    single { CreateTaskUseCase(get()) }
    single { UpdateTaskUseCase(get()) }
    single { DeleteTaskUseCase(get()) }
    single { GetTaskUseCase(get()) }
    single { GetTasksByProjectUseCase(get()) }
}