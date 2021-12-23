package info.erulinman.litetimetracker.di.module

import dagger.Binds
import dagger.Module
import info.erulinman.litetimetracker.data.DatabaseRepository
import info.erulinman.litetimetracker.data.DatabaseRepositoryImpl

@Module
interface BindsModule {

    @Binds
    fun bindDatabaseRepositoryImplToDatabaseRepository(
        databaseRepositoryImpl: DatabaseRepositoryImpl
    ): DatabaseRepository
}