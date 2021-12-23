package info.erulinman.litetimetracker.di

import android.app.Service
import androidx.fragment.app.Fragment
import info.erulinman.litetimetracker.MainApplication

val Service.appComponent: AppComponent
    get() = (application as MainApplication).appComponent

val Fragment.appComponent: AppComponent
    get() = (requireContext().applicationContext as MainApplication).appComponent