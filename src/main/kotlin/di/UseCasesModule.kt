package di

import logic.usecase.auditLog.ManageAuditLogUseCase
import logic.usecase.auth.AuthenticateUserLoginUseCase
import logic.usecase.auth.CreateMateUseCase
import logic.usecase.project.GetProjectStateUseCase
import org.damascus.logic.usecase.auth.ManageMateUseCase
import org.damascus.logic.usecase.project.ManageMateAssignmentUseCase
import org.damascus.logic.usecase.project.ManageProjectUseCase
import org.damascus.logic.usecase.state.ManageTaskStateUseCase
import org.damascus.logic.usecase.task.ManageTaskUseCase
import org.koin.dsl.module

val useCaseModule = module {
    // Project use cases
    single { ManageProjectUseCase(get(), get()) }
    single { ManageMateAssignmentUseCase(get()) }
    single { GetProjectStateUseCase(get(), get()) }

    // Authentication use cases
    single { CreateMateUseCase(get()) }
    single { AuthenticateUserLoginUseCase(get()) }
    single { ManageMateUseCase(get()) }

    // Task State use cases
    single { ManageTaskStateUseCase(get()) }

    // Task use cases
    single { ManageTaskUseCase(get()) }

    // audit Log Use cases
    single { ManageAuditLogUseCase(get()) }

}