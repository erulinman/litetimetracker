package info.erulinman.litetimetracker.features.timer

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import info.erulinman.litetimetracker.databinding.FragmentExitBinding

class ExitFragment: DialogFragment() {

    private var _binding: FragmentExitBinding? = null
    private val binding: FragmentExitBinding
        get() {
            checkNotNull(_binding)
            return _binding as FragmentExitBinding
        }

    private lateinit var dialog: AlertDialog

    val isShowing: Boolean
        get() = if (this::dialog.isInitialized)
            dialog.isShowing else false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val positiveButtonClickListener = DialogInterface.OnClickListener { _, which ->
            parentFragmentManager.setFragmentResult(
                REQUEST_KEY, bundleOf(
                RESPONSE_KEY to which
            ))
        }

        _binding = FragmentExitBinding.inflate(LayoutInflater.from(context)).apply {
            positiveButton.setOnClickListener {
                positiveButtonClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE)
                dismiss()
            }
            cancelButton.setOnClickListener { dismiss() }
        }

        dialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return dialog
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val TAG = "ExitFragment.TAG"
        const val REQUEST_KEY = "ExitFragment.REQUEST_KEY"
        const val RESPONSE_KEY = "ExitFragment.RESPONSE_KEY"

        fun show(manager: FragmentManager) = ExitFragment().show(manager, TAG)
    }
}