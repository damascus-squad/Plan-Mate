package org.damascus.di

import data.csv.helpers.UserCsvHelper
import logic.model.User
import org.damascus.data.DataSource
import org.damascus.data.authentication.AuthenticationRepoImpl
import org.damascus.data.authentication.MD5HashingService
import org.damascus.data.csv.CsvDataSource
import org.damascus.data.csv.generateCsvHeader
import org.damascus.data.csv.utils.CsvConstants.USERS_FILE
import org.damascus.logic.AuthenticationRepository
import org.damascus.logic.HashingService
import org.damascus.logic.usecase.AuthenticationUseCase
import org.koin.dsl.module

val appModule = module {

    single<DataSource<User>> {
        CsvDataSource(
            USERS_FILE,
            { generateCsvHeader<User>() },
            extractId = { it.id },
            parser = UserCsvHelper::parseUser,
            serializer = UserCsvHelper::serializeUser
        )
    }

    single<AuthenticationRepository> { AuthenticationRepoImpl(get(), get()) }
    single<HashingService> { MD5HashingService() }
    single { AuthenticationUseCase(get()) }

}
