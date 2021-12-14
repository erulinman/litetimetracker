package info.erulinman.lifetimetracker.di

import dagger.Component
import info.erulinman.lifetimetracker.TimerService
import info.erulinman.lifetimetracker.di.module.AppModule
import info.erulinman.lifetimetracker.fragments.CategoryListFragment
import info.erulinman.lifetimetracker.fragments.PresetListFragment
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    fun inject(fragment: CategoryListFragment)
    fun inject(fragment: PresetListFragment)
    fun inject(timerService: TimerService)
}