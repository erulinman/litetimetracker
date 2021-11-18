package info.erulinman.lifetimetracker

import info.erulinman.lifetimetracker.data.entity.Preset

interface Navigator {

    /**
     * Used to set default state of bar.
     *
     * Default state:
     *  - visible title without onClickListener
     *  - fab with icon and action
     */
    fun updateAppBar(iconRes: Int, title: String, action: () -> Unit)

    fun updateAppBarTitle(title: String)

    fun updateAppBarTitle(visibility: Boolean = false)

    /**
     * Used in TimerFragment to set icon and fab action when timer state changes.
     */
    fun updateFabOnAppBar(iconRes: Int, action: () -> Unit)

    fun setOnClickListenerToAppBarTitle(actionOnClick: (() -> Unit)?)

    fun bindTimerService()

    fun unbindTimerService()

    fun enableBroadcast()

    fun disableBroadcast()

    fun setServiceConnection(presets: List<Preset>)

    fun getTimerService(): TimerService?

    fun onStart()

    fun onBackPressed()

    fun showToast(stringRes: Int)

    fun setExitFragmentListener()
}