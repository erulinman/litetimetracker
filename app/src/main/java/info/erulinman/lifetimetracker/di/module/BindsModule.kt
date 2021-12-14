package info.erulinman.lifetimetracker.di.module

import dagger.Binds
import dagger.Module
import info.erulinman.lifetimetracker.data.DatabaseRepository
import info.erulinman.lifetimetracker.data.DatabaseRepositoryImpl

@Module
interface BindsModule {

    @Binds
    fun bindDatabaseRepositoryImplToDatabaseRepository(
        databaseRepositoryImpl: DatabaseRepositoryImpl
    ): DatabaseRepository
}