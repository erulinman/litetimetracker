package info.erulinman.lifetimetracker.di.module

import android.content.Context
import dagger.Module
import dagger.Provides
import info.erulinman.lifetimetracker.data.database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module(includes = [BindsModule::class])
class AppModule(
    private val applicationContext: Context,
    private val applicationScope: CoroutineScope
) {

    @Singleton
    @Provides
    fun provideApplicationContext(): Context = applicationContext

    @Singleton
    @Provides
    fun provideApplicationScope(): CoroutineScope = applicationScope

    @Provides
    fun provideAppDatabase(
        context: Context,
        scope: CoroutineScope
    ): AppDatabase {
        return AppDatabase.getInstance(context, scope)
    }
}