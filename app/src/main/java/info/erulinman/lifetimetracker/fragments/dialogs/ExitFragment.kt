package info.erulinman.lifetimetracker.fragments.dialogs

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
import info.erulinman.lifetimetracker.databinding.FragmentExitBinding

class ExitFragment: DialogFragment() {
    lateinit var binding: FragmentExitBinding
    lateinit var dialog: AlertDialog
    private val isShowing: Boolean
        get() = if (this::dialog.isInitialized) dialog.isShowing else false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val positiveButtonClickListener = DialogInterface.OnClickListener { dialog, which ->
            parentFragmentManager.setFragmentResult(REQUEST_KEY, bundleOf(
                RESPONSE_KEY to which
            ))
        }

        binding = FragmentExitBinding.inflate(LayoutInflater.from(context)).apply {
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

    companion object {
        private const val TAG = "ExitFragment.TAG"

        const val REQUEST_KEY = "ExitFragment.REQUEST_KEY"
        const val RESPONSE_KEY = "ExitFragment.RESPONSE_KEY"

        private val dialogFragment = ExitFragment()

        val isShowing: Boolean
            get() = dialogFragment.isShowing

        fun close() = dialogFragment.dismiss()

        fun show(manager: FragmentManager) = dialogFragment.show(manager, TAG)
    }
}