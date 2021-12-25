package info.erulinman.litetimetracker

import info.erulinman.litetimetracker.data.entity.Preset

interface Navigator {

    fun updateToolbar(title: String, actionIconRes: Int, action: () -> Unit)

    fun setToolbarActionVisibility(visibility: Boolean)

    fun updateTitle(title: String)

    fun updateTitle(visibility: Boolean)

    fun bindTimerService()

    fun unbindTimerService()

    fun enableBroadcast()

    fun disableBroadcast()

    fun setServiceConnection(presets: List<Preset>?)

    fun showToast(stringRes: Int)

    fun setExitFragmentListener()
}