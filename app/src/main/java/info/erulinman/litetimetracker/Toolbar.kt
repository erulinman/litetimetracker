package info.erulinman.litetimetracker

interface Toolbar {

    fun updateToolbar(title: String, actionIconRes: Int, action: () -> Unit)

    fun setActionVisibility(visibility: Boolean)

    fun updateTitle(title: String)

    fun updateTitle(stringResId: Int)

    fun updateTitle(visibility: Boolean)
}