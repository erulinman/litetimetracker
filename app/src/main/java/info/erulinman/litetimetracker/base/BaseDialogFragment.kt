package info.erulinman.litetimetracker.base

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding

abstract class BaseDialogFragment<VB : ViewBinding> : DialogFragment() {

    private var _dialog: AlertDialog? = null
    protected val dialog get() = checkNotNull(_dialog)

    private var _binding: VB? = null
    protected val binding get() = checkNotNull(_binding)

    protected abstract fun initBinding(): VB

    protected open fun setupOnCreateDialog(savedInstanceState: Bundle?) {}

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = initBinding()
        _dialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()
        setupOnCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}