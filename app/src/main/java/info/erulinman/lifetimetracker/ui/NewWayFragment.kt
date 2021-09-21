package info.erulinman.lifetimetracker.ui

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import info.erulinman.lifetimetracker.R
import info.erulinman.lifetimetracker.databinding.FragmentNewWayBinding

class NewWayFragment: DialogFragment() {
    private lateinit var binding: FragmentNewWayBinding
    private lateinit var dialog: AlertDialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = FragmentNewWayBinding.inflate(LayoutInflater.from(context))

        val saveButtonClickListener = DialogInterface.OnClickListener { _, which ->
            val wayName = binding.wayNameInput.text.toString().ifEmpty {
                getString(R.string.default_way_name)
            }
            val wayDescription = binding.wayDescriptionInput.toString().ifEmpty { null }

            parentFragmentManager.setFragmentResult(REQUEST_KEY, bundleOf(
                RESPONSE_KEY to which,
                WAY_NAME to wayName,
                WAY_DESCRIPTION to wayDescription,
            )
            )
        }

        binding.apply {
            saveButton.setOnClickListener {
                saveButtonClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE)
                dismiss()
            }
            cancelButton.setOnClickListener {
                dismiss()
            }
        }

        dialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return dialog
    }

    override fun onResume() {
        super.onResume()
        dialog.window?.apply {
            val updatedAttributes = attributes.apply {
                width = resources.getDimensionPixelSize(R.dimen.layout_width_preset_editor_dialog)
            }
            attributes = updatedAttributes
        }
    }

    companion object {
        const val TAG = "info.erulinman.lifetimetracker.NewWayFragment"
        const val REQUEST_KEY = "info.erulinman.lifetimetracker.REQUEST_KEY"
        const val RESPONSE_KEY = "info.erulinman.lifetimetracker.RESPONSE_KEY"
        const val WAY_NAME = "info.erulinman.lifetimetracker.WAY_NAME"
        const val WAY_DESCRIPTION = "info.erulinman.lifetimetracker.WAY_DESCRIPTION"
    }
}