package info.erulinman.litetimetracker.features.categories

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.os.bundleOf
import com.google.android.material.snackbar.Snackbar
import info.erulinman.litetimetracker.R
import info.erulinman.litetimetracker.base.BaseDialogFragment
import info.erulinman.litetimetracker.data.entity.Category
import info.erulinman.litetimetracker.databinding.FragmentAddCategoryBinding

class CategoryEditorFragment : BaseDialogFragment<FragmentAddCategoryBinding>() {

    private var category: Category? = null

    override fun initBinding() =
        FragmentAddCategoryBinding.inflate(LayoutInflater.from(context))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        category = arguments?.getParcelable(ARG_CATEGORY)
    }

    override fun setupOnCreateDialog(savedInstanceState: Bundle?) {
        category?.let {
            binding.editCategoryName.setText(it.name)
        }

        val saveButtonClickListener = DialogInterface.OnClickListener { _, which ->
            val currentCategoryName = binding.editCategoryName.text.toString()
            val categoryId = category?.id // null if fragment used to add new category
            val position = category?.position

            parentFragmentManager.setFragmentResult(
                REQUEST_KEY,
                bundleOf(
                    RESPONSE_KEY to which,
                    CATEGORY_NAME to currentCategoryName,
                    CATEGORY_ID to categoryId,
                    CATEGORY_POSITION to position
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

    companion object {
        private const val ARG_CATEGORY = "CategoryEditorFragment.ARG_CATEGORY"

        const val TAG = "CategoryEditorFragment.TAG"
        const val REQUEST_KEY = "CategoryEditorFragment.REQUEST_KEY"
        const val RESPONSE_KEY = "CategoryEditorFragment.RESPONSE_KEY"
        const val CATEGORY_NAME = "CategoryEditorFragment.CATEGORY_NAME"
        const val CATEGORY_ID = "CategoryEditorFragment.CATEGORY_ID"
        const val CATEGORY_POSITION = "CategoryEditorFragment.CATEGORY_POSITION"

        fun getInstanceWithArg(category: Category) = CategoryEditorFragment().apply {
            arguments = bundleOf(ARG_CATEGORY to category)
        }
    }
}