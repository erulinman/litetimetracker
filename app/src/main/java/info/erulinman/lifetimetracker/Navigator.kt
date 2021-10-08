package info.erulinman.lifetimetracker

import info.erulinman.lifetimetracker.data.entity.Preset

interface Navigator {
    fun updateAppBar(iconRes: Int, title: String, action: () -> Unit)
    fun updateAppBar(title: String)
    fun updateAppBar(
        iconRes: Int,
        titleIsVisible: Boolean = true,
        action: () -> Unit
    )
    fun bindTimerService()
    fun unbindTimerService()
    fun enableBroadcast()
    fun disableBroadcast()
    fun setServiceConnection(presets: List<Preset>)
    fun getTimerService(): TimerService?
    fun onBackPressed()
    fun onResume()
    fun showToast(stringRes: Int)
}