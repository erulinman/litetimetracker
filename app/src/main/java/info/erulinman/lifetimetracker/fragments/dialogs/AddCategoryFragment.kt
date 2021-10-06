package info.erulinman.lifetimetracker.fragments.dialogs

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.snackbar.Snackbar
import info.erulinman.lifetimetracker.R
import info.erulinman.lifetimetracker.databinding.FragmentAddCategoryBinding

class AddCategoryFragment: DialogFragment() {
    private lateinit var binding: FragmentAddCategoryBinding
    private lateinit var dialog: AlertDialog
    private lateinit var categoryName: String

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = FragmentAddCategoryBinding.inflate(LayoutInflater.from(context))

        val saveButtonClickListener = DialogInterface.OnClickListener { _, which ->
            parentFragmentManager.setFragmentResult(
                REQUEST_KEY, bundleOf(
                RESPONSE_KEY to which,
                CATEGORY_NAME to categoryName
            ))
        }

        binding.apply {
            saveButton.setOnClickListener {
                categoryName = binding.editCategoryName.text.toString()
                if (categoryName.isEmpty()) {
                    Snackbar.make(
                        binding.root,
                        R.string.snackbar_message_empty_category_name,
                        Snackbar.LENGTH_SHORT
                    ).show()
                } else {
                    saveButtonClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE)
                    dismiss()
                }
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
            binding.editCategoryName.focusAndShowKeyboard()
        }
    }

    private fun EditText.focusAndShowKeyboard() {
        fun EditText.showTheKeyboardNow() {
            if (isFocused) {
                post {
                    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
                }
            }
        }

        requestFocus()
        if (hasWindowFocus()) {
            showTheKeyboardNow()
        } else {
            viewTreeObserver.addOnWindowFocusChangeListener(
                object : ViewTreeObserver.OnWindowFocusChangeListener {
                    override fun onWindowFocusChanged(hasFocus: Boolean) {
                        if (hasFocus) {
                            this@focusAndShowKeyboard.showTheKeyboardNow()
                            viewTreeObserver.removeOnWindowFocusChangeListener(this)
                        }
                    }
                })
        }
    }

    companion object {
        const val TAG = "info.erulinman.lifetimetracker.AddCategoryFragment"
        const val REQUEST_KEY = "info.erulinman.lifetimetracker.REQUEST_KEY"
        const val RESPONSE_KEY = "info.erulinman.lifetimetracker.RESPONSE_KEY"
        const val CATEGORY_NAME = "info.erulinman.lifetimetracker.CATEGORY_NAME"

        fun show(manager: FragmentManager) {
            val dialogFragment = AddCategoryFragment()
            dialogFragment.show(manager, TAG)
        }
    }
}