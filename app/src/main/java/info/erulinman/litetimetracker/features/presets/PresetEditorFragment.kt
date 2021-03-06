package info.erulinman.litetimetracker.features.presets

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import info.erulinman.litetimetracker.R
import info.erulinman.litetimetracker.base.BaseDialogFragment
import info.erulinman.litetimetracker.data.entity.Preset
import info.erulinman.litetimetracker.databinding.FragmentPresetEditorBinding
import info.erulinman.litetimetracker.utils.toListHHMMSS
import info.erulinman.litetimetracker.utils.toStringOfTwoChar
import java.util.concurrent.TimeUnit

class PresetEditorFragment : BaseDialogFragment<FragmentPresetEditorBinding>() {

    private var preset: Preset? = null

    override fun initBinding() =
        FragmentPresetEditorBinding.inflate(LayoutInflater.from(context))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preset = arguments?.getParcelable(ARG_PRESET)
    }

    override fun setupOnCreateDialog(savedInstanceState: Bundle?) {
        preset?.let { preset ->
            val (hours, minutes, seconds) = preset.time.toListHHMMSS()
            binding.apply {
                editPresetName.setText(preset.name)
                hoursInput.setText(hours)
                minutesInput.setText(minutes)
                secondsInput.setText(seconds)
            }
        }

        val saveButtonClickListener = DialogInterface.OnClickListener { _, which ->
            val presetName = binding.editPresetName.text.toString().ifEmpty {
                getString(R.string.default_preset_name)
            }
            val presetTime = getPresetTimeInLong()

            // both null if fragment used for create new preset
            val presetId = preset?.id
            val categoryId = preset?.categoryId
            val position = preset?.position

            parentFragmentManager.setFragmentResult(
                REQUEST_KEY,
                bundleOf(
                    RESPONSE_KEY to which,
                    PRESET_ID to presetId,
                    CATEGORY_ID to categoryId,
                    PRESET_POSITION to position,
                    PRESET_NAME to presetName,
                    PRESET_TIME to presetTime,
                )
            )
        }

        val focusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
            if (hasFocus) (view as EditText).text.clear()
        }

        binding.apply {
            saveButton.setOnClickListener {
                saveButtonClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE)
                dismiss()
            }
            cancelButton.setOnClickListener {
                dismiss()
            }
            hoursInput.apply {
                onFocusChangeListener = focusChangeListener
                doOnTextChanged { text, _, _, _ ->
                    checkTimeExceeded(text, 24)
                }
            }
            minutesInput.apply {
                onFocusChangeListener = focusChangeListener
                doOnTextChanged { text, _, _, _ ->
                    checkTimeExceeded(text, 60, HOURS)
                }
            }
            secondsInput.apply {
                onFocusChangeListener = focusChangeListener
                doOnTextChanged { text, _, _, _ ->
                    checkTimeExceeded(text, 60, MINUTES)
                }
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
        }
    }

    private fun EditText.checkTimeExceeded(
        s: CharSequence?,
        default: Int,
        transferTo: Int? = null
    ) {
        val time = s.toString().toLongOrNull() ?: 0

        transferTo?.let {
            when (it) {
                MINUTES -> {
                    val seconds = binding.secondsInput.text.toString().toLongOrNull() ?: 0
                    if (seconds >= default) {
                        var minutes = binding.minutesInput.text.toString().toLongOrNull() ?: 0
                        minutes++
                        binding.minutesInput.setText(minutes.toString())
                        val remainder = seconds - default
                        binding.secondsInput.setText(remainder.toStringOfTwoChar())
                        setSelection(SELECTION)
                    }
                }
                HOURS -> {
                    val minutes = binding.minutesInput.text.toString().toLongOrNull() ?: 0
                    if (minutes >= default) {
                        var hours = binding.hoursInput.text.toString().toLongOrNull() ?: 0
                        hours++
                        binding.hoursInput.setText(hours.toString())
                        val remainder = minutes - default
                        binding.minutesInput.setText(remainder.toStringOfTwoChar())
                        setSelection(SELECTION)
                    }
                }
            }
        } ?: run {
            if (time > default) {
                setText(default.toString())
                setSelection(SELECTION)
            }
        }
    }

    private fun getPresetTimeInLong(): Long {
        val hours = binding.hoursInput.text.toString().toLongOrNull()?.let {
            TimeUnit.HOURS.toMillis(it)
        } ?: 0
        val minutes = binding.minutesInput.text.toString().toLongOrNull()?.let {
            TimeUnit.MINUTES.toMillis(it)
        } ?: 0
        val seconds = binding.secondsInput.text.toString().toLongOrNull()?.let {
            TimeUnit.SECONDS.toMillis(it)
        } ?: 0

        val time = hours + minutes + seconds

        return if (time != 0L) time else DEFAULT_TIME
    }

    companion object {
        private const val MINUTES = 1
        private const val HOURS = 2
        private const val SELECTION: Int = 2
        private const val ARG_PRESET = "PresetEditorFragment.ARG_PRESET"
        private const val DEFAULT_TIME = 1500000L

        const val TAG = "PresetEditorFragment.TAG"
        const val REQUEST_KEY = "PresetEditorFragment.REQUEST_KEY"
        const val RESPONSE_KEY = "PresetEditorFragment.RESPONSE_KEY"
        const val PRESET_ID = "PresetEditorFragment.PRESET_ID"
        const val CATEGORY_ID = "PresetEditorFragment.CATEGORY_ID"
        const val PRESET_NAME = "PresetEditorFragment.PRESET_NAME"
        const val PRESET_TIME = "PresetEditorFragment.PRESET_TIME"
        const val PRESET_POSITION = "PresetEditorFragment.PRESET_POSITION"

        fun getInstanceWithArg(preset: Preset) = PresetEditorFragment().apply {
            arguments = bundleOf(ARG_PRESET to preset)
        }
    }
}
