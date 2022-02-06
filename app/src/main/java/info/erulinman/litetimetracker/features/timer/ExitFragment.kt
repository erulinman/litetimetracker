package info.erulinman.litetimetracker.features.timer

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import androidx.core.os.bundleOf
import info.erulinman.litetimetracker.base.BaseDialogFragment
import info.erulinman.litetimetracker.databinding.FragmentExitBinding

class ExitFragment : BaseDialogFragment<FragmentExitBinding>() {

    override fun initBinding() =
        FragmentExitBinding.inflate(LayoutInflater.from(context))

    override fun setupOnCreateDialog(savedInstanceState: Bundle?) {
        val positiveButtonClickListener = DialogInterface.OnClickListener { _, which ->
            parentFragmentManager.setFragmentResult(
                REQUEST_KEY,
                bundleOf(RESPONSE_KEY to which)
            )
        }

        binding.positiveButton.setOnClickListener {
            positiveButtonClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE)
            dismiss()
        }
        binding.cancelButton.setOnClickListener { dismiss() }
    }

    companion object {
        const val TAG = "ExitFragment.TAG"
        const val REQUEST_KEY = "ExitFragment.REQUEST_KEY"
        const val RESPONSE_KEY = "ExitFragment.RESPONSE_KEY"
    }
}