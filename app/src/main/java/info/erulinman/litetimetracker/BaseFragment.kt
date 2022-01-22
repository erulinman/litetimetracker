package info.erulinman.litetimetracker

import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<VB: ViewBinding>(contentLayoutId: Int) :
    Fragment(contentLayoutId) {

    protected var _binding: VB? = null
    protected val binding: VB
        get() {
            checkNotNull(_binding)
            return _binding as VB
        }

    protected val toolbar: Toolbar
        get() {
            return requireActivity() as Toolbar
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    open fun onBackPressed(): Boolean  = true
}