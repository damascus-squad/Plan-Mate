package di

import logic.usecase.auditLog.GetLogsByProjectIdUseCase
import logic.usecase.auditLog.SaveLogUseCase
import logic.usecase.auth.AuthenticateUserLoginUseCase
import logic.usecase.auth.CreateMateUseCase
import logic.usecase.project.*
import logic.usecase.state.*
import logic.usecase.task.*
import org.damascus.logic.usecase.auth.GetAllMatesUseCase
import org.damascus.logic.usecase.project.UnassignMateUseCase
import org.koin.dsl.module

val useCaseModule = module {
    // Project use cases
    single { CreateProjectUseCase(get(), get()) }
    single { GetAdminProjectsUseCase(get()) }
    single { DeleteProjectUseCase(get()) }
    single { UpdateProjectUseCase(get()) }
    single { GetMateProjectsUseCase(get()) }
    single { GetProjectUseCase(get()) }
    single { AssignMateUseCase(get()) }
    single { UnassignMateUseCase(get()) }
    single { GetProjectStateUseCase(get(), get()) }

    // Authentication use cases
    single { CreateMateUseCase(get()) }
    single { AuthenticateUserLoginUseCase(get()) }
    single { GetAllMatesUseCase(get())}
    single { AssignMateUseCase(get()) }
    single { UnassignMateUseCase(get()) }

    // Task State use cases
    single { CheckTaskStateExistsUseCase(get()) }
    single { CreateTaskStateUseCase(get()) }
    single { DeleteTaskStateUseCase(get()) }
    single { GetAllTaskStatesUseCase(get()) }
    single { GetTaskStateByIdUseCase(get()) }
    single { IncrementTaskStateReferencesUseCase(get()) }
    single { UpdateTaskStateUseCase(get()) }

    // Task use cases
    single { CreateTaskUseCase(get()) }
    single { UpdateTaskUseCase(get()) }
    single { DeleteTaskUseCase(get()) }
    single { GetTaskUseCase(get()) }
    single { GetTasksByProjectUseCase(get()) }

    single { GetTaskStateByIdUseCase(get()) }
    single { SaveLogUseCase(get()) }
    single { GetLogsByProjectIdUseCase(get()) }

}