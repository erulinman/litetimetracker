package info.erulinman.litetimetracker

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<VB : ViewBinding>(contentLayoutId: Int) :
    Fragment(contentLayoutId) {

    protected var _binding: VB? = null
    protected val binding: VB
        get() {
            checkNotNull(_binding)
            return _binding as VB
        }

    private var _navigator: Navigator? = null
    protected val navigator get() = checkNotNull(_navigator)

    private var _toolbar: Toolbar? = null
    protected val toolbar: Toolbar
        get() {
            checkNotNull(_toolbar)
            return _toolbar as Toolbar
        }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        _toolbar = requireActivity() as Toolbar
        _navigator = context as Navigator
    }

    override fun onDetach() {
        super.onDetach()
        _navigator = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    open fun onBackPressed(): Boolean = true
}