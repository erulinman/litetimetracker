package info.erulinman.litetimetracker.features.presets

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
import info.erulinman.litetimetracker.R
import info.erulinman.litetimetracker.data.entity.Category
import info.erulinman.litetimetracker.databinding.FragmentAddCategoryBinding

class CategoryEditorFragment : DialogFragment() {

    private var _binding: FragmentAddCategoryBinding? = null
    private val binding: FragmentAddCategoryBinding
        get() {
            checkNotNull(_binding)
            return _binding as FragmentAddCategoryBinding
        }

    private lateinit var dialog: AlertDialog

    private var category: Category? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        category = arguments?.getParcelable(ARG_CATEGORY)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentAddCategoryBinding.inflate(LayoutInflater.from(context))

        category?.let {
            binding.editCategoryName.setText(it.name)
        }

        val saveButtonClickListener = DialogInterface.OnClickListener { _, which ->
            val currentCategoryName = binding.editCategoryName.text.toString()
            val categoryId = category?.id // null if fragment used to add new category

            parentFragmentManager.setFragmentResult(
                REQUEST_KEY,
                bundleOf(
                    RESPONSE_KEY to which,
                    CATEGORY_NAME to currentCategoryName,
                    CATEGORY_ID to categoryId
                )
            )
        }

        binding.apply {
            saveButton.setOnClickListener {
                val categoryName = binding.editCategoryName.text.toString()
                if (categoryName.isEmpty()) {
                    Snackbar.make(
                        binding.root,
                        R.string.snackbar_empty_category_name,
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
                    val imm =
                        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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
                }
            )
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val ARG_CATEGORY = "CategoryEditorFragment.ARG_CATEGORY"
        private const val TAG = "CategoryEditorFragment.AddCategoryFragment"

        const val REQUEST_KEY = "CategoryEditorFragment.REQUEST_KEY"
        const val RESPONSE_KEY = "CategoryEditorFragment.RESPONSE_KEY"
        const val CATEGORY_NAME = "CategoryEditorFragment.CATEGORY_NAME"
        const val CATEGORY_ID = "CategoryEditorFragment.CATEGORY_ID"

        fun show(manager: FragmentManager, category: Category? = null) {
            val dialogFragment = CategoryEditorFragment()
            category?.let {
                dialogFragment.arguments = bundleOf(ARG_CATEGORY to it)
            }
            dialogFragment.show(manager, TAG)
        }
    }
}