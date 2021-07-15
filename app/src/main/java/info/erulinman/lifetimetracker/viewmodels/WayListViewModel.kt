package info.erulinman.lifetimetracker.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import info.erulinman.lifetimetracker.data.WayRepository

class WayListViewModel(private val wayRepository: WayRepository) : ViewModel() {
    val ways = MutableLiveData(wayRepository.getAll())
}