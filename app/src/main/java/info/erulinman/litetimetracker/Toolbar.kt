package info.erulinman.litetimetracker

interface Toolbar {

    fun updateToolbar(title: String, actionIconRes: Int, action: () -> Unit)

    fun setToolbarActionVisibility(visibility: Boolean)

    fun updateTitle(title: String)

    fun updateTitle(visibility: Boolean)

    fun showToast(stringRes: Int)
}