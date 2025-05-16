package org.damascus.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

@Module
@ComponentScan(value = "org.damascus")
class KoinAppModule