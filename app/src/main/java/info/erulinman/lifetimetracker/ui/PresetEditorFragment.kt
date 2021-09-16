package info.erulinman.lifetimetracker.ui

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import info.erulinman.lifetimetracker.R
import info.erulinman.lifetimetracker.data.entity.Preset
import info.erulinman.lifetimetracker.databinding.FragmentPresetEditorBinding
import info.erulinman.lifetimetracker.utilities.toListHHMMSS
import java.util.concurrent.TimeUnit

class PresetEditorFragment(private val preset: Preset? = null) : DialogFragment() {
    private lateinit var binding: FragmentPresetEditorBinding
    private lateinit var dialog: android.app.AlertDialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = FragmentPresetEditorBinding.inflate(LayoutInflater.from(context))
        preset?.let {
            binding.apply {
                presetNameInput.setText(it.name)
                val (hours, minutes, seconds) = it.time.toListHHMMSS()
                hoursInput.setText(hours)
                minutesInput.setText(minutes)
                secondsInput.setText(seconds)
            }
        }

        val saveButtonClickListener = DialogInterface.OnClickListener { _, which ->
            val presetName = binding.presetNameInput.text.toString().ifEmpty {
                getString(R.string.default_preset_name)
            }
            var update = false
            var presetId: Long? = null
            var wayId: Long? = null
            val presetTime = getPresetTimeInLong()

            preset?.let {
                presetId = it.id
                wayId = it.wayId
                update = true
            }

            parentFragmentManager.setFragmentResult(REQUEST_KEY, bundleOf(
                RESPONSE_KEY to which,
                PRESET_ID to presetId,
                WAY_ID to wayId,
                PRESET_NAME to presetName,
                PRESET_TIME to presetTime,
                UPDATE to update
            ))
        }

        val focusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
            if (hasFocus)  (view as EditText).text.clear()
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

        dialog = android.app.AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return dialog
    }

    override fun onResume() {
        super.onResume()
        dialog.window?.apply {
            val updatedAttributes = attributes.apply {
                width = resources.getDimensionPixelSize(R.dimen.preset_editor_dialog_width)
            }
            attributes = updatedAttributes
        }
    }

    private fun EditText.checkTimeExceeded(s: CharSequence?, default: Int, transferTo: Int? = null) {
        val time = s.toString().toLongOrNull() ?: 0

        transferTo?.let {
            when (it) {
                MINUTES -> {
                    val seconds = binding.secondsInput.text.toString().toLongOrNull() ?: 0
                    if (seconds >= default) {
                        var minutes = binding.minutesInput.text.toString().toLongOrNull() ?: 0
                        minutes++
                        binding.minutesInput.setText(minutes.toString())
                        binding.secondsInput.setText(ZERO_TIME_VALUE)
                        setSelection(SELECTION)
                    }
                }
                HOURS -> {
                    val minutes = binding.minutesInput.text.toString().toLongOrNull() ?: 0
                    if (minutes >= default) {
                        var hours = binding.hoursInput.text.toString().toLongOrNull() ?: 0
                        hours++
                        binding.hoursInput.setText(hours.toString())
                        binding.minutesInput.setText(ZERO_TIME_VALUE)
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

        return hours + minutes + seconds
    }

    companion object {
        private const val MINUTES = 1
        private const val HOURS = 2
        private const val SELECTION: Int = 2
        private const val ZERO_TIME_VALUE = "00"

        const val TAG = "info.erulinman.lifetimetracker.PresetFragment"
        const val REQUEST_KEY = "info.erulinman.lifetimetracker.REQUEST_KEY"
        const val RESPONSE_KEY = "info.erulinman.lifetimetracker.RESPONSE_KEY"
        const val PRESET_ID = "info.erulinman.lifetimetracker.PRESET_ID"
        const val WAY_ID = "info.erulinman.lifetimetracker.WAY_ID"
        const val PRESET_NAME = "info.erulinman.lifetimetracker.PRESET_NAME"
        const val PRESET_TIME = "info.erulinman.lifetimetracker.PRESET_TIME"
        const val UPDATE = "info.erulinman.lifetimetracker.UPDATE"
    }
}
