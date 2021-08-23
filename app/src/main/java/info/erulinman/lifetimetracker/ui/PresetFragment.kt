package info.erulinman.lifetimetracker.ui;

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import info.erulinman.lifetimetracker.data.entity.Preset
import info.erulinman.lifetimetracker.databinding.FragmentPresetBinding
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class PresetFragment(private val preset: Preset? = null) : DialogFragment() {
    private lateinit var binding: FragmentPresetBinding
    private var updatedPresetInString: String? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = FragmentPresetBinding.inflate(LayoutInflater.from(context))
        preset?.let {
            binding.apply {
                presetName.setText(preset.name)
                presetTime.setText(preset.time.fromLongToTimerString())
            }
        }

        val listener = DialogInterface.OnClickListener { _, which ->
            val presetName = binding.presetName.text.toString().ifEmpty { null }
            val presetTime = binding.presetTime.text.toString().ifEmpty { null }
            updatedPresetInString = preset?.let { oldPreset ->
                Json.encodeToString(
                    Preset(
                        id = oldPreset.id,
                        wayId = oldPreset.wayId,
                        name = binding.presetName.text.toString(),
                        time = binding.presetTime.text.toString().toLong()
                    )
                )
            }

            parentFragmentManager.setFragmentResult(REQUEST_KEY, bundleOf(
                KEY_RESPONSE to which,
                PRESET_NAME to presetName,
                PRESET_TIME to presetTime,
                UPDATED_PRESET to updatedPresetInString
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
        @JvmStatic val KEY_RESPONSE = "info.erulinman.lifetimetracker.KEY_RESPONSE"
        @JvmStatic val PRESET_NAME = "info.erulinman.lifetimetracker.PRESET_NAME"
        @JvmStatic val PRESET_TIME = "info.erulinman.lifetimetracker.PRESET_TIME"
        @JvmStatic val UPDATED_PRESET = "info.erulinman.lifetimetracker.UPDATED_PRESET"
    }
}
