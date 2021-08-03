package info.erulinman.lifetimetracker.ui;

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import info.erulinman.lifetimetracker.R
import info.erulinman.lifetimetracker.databinding.FragmentPresetBinding
import info.erulinman.lifetimetracker.utilities.DEBUG_TAG

class PresetFragment() : DialogFragment() {
    private lateinit var binding: FragmentPresetBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = FragmentPresetBinding.inflate(LayoutInflater.from(context))
        val listener = DialogInterface.OnClickListener { _, which ->
            val presetName = binding.presetName.text.toString().ifEmpty { null }
            val presetTime = binding.presetTime.text.toString().ifEmpty { null }
            parentFragmentManager.setFragmentResult(REQUEST_KEY, bundleOf(
                KEY_RESPONSE to which,
                PRESET_NAME to presetName,
                PRESET_TIME to presetTime
            ))
        }
        return AlertDialog.Builder(requireContext())
            .setCancelable(true)
            .setView(binding.root)
            .setPositiveButton("Save", listener)
            .setNegativeButton("Cancel", listener)
            .show()
    }

    companion object {
        @JvmStatic val TAG: String = PresetFragment::class.java.simpleName
        @JvmStatic val REQUEST_KEY = "$TAG.defaultRequestKey"
        @JvmStatic val KEY_RESPONSE = "response"
        @JvmStatic val PRESET_NAME = "preset name"
        @JvmStatic val PRESET_TIME = "preset time"
    }
}
