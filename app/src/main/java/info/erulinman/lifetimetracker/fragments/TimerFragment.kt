package info.erulinman.lifetimetracker.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import info.erulinman.lifetimetracker.R
import info.erulinman.lifetimetracker.TimerService
import info.erulinman.lifetimetracker.data.entity.Preset
import info.erulinman.lifetimetracker.databinding.FragmentTimerBinding
import info.erulinman.lifetimetracker.utilities.DEBUG_TAG

class TimerFragment : Fragment(R.layout.fragment_timer) {

    private var _binding: FragmentTimerBinding? = null
    private val binding: FragmentTimerBinding
        get() {
            checkNotNull(_binding)
            return _binding as FragmentTimerBinding
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(DEBUG_TAG, "TimerFragment.onCreate()")

        requireArguments().getParcelableArrayList<Preset>(ARG_PRESET_LIST)?.let { presets ->
            navigator().setServiceConnection(presets)
        }

        navigator().enableBroadcast()

        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentTimerBinding.bind(view)
        navigator().setOnClickListenerToAppBarTitle(null)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    fun setObservers(timerService: TimerService) {
        Log.d(DEBUG_TAG, "TimerFragment.setObservers()")
        timerService.apply {
            presetName.observe(viewLifecycleOwner) { presetName ->
                navigator().updateAppBarTitle(presetName)
            }
            time.observe(viewLifecycleOwner) { time ->
                binding.timer.text = time
            }
            state.observe(viewLifecycleOwner) { state -> when (state) {
                TimerService.INITIALIZED -> startTimer()
                TimerService.STOPPED -> {
                    navigator().updateFabOnAppBar(R.drawable.ic_play) { startTimer() }
                    navigator().updateAppBarTitle(true)
                }
                TimerService.STARTED -> {
                    navigator().updateFabOnAppBar(R.drawable.ic_pause) { stopTimer() }
                    navigator().updateAppBarTitle(true)
                }
                TimerService.FINISHED -> {
                    navigator().updateFabOnAppBar(R.drawable.ic_restart) { restartPresets() }
                    navigator().updateAppBarTitle(false)
                }
            }}
            canSkip.observe(viewLifecycleOwner) { canSkip ->
                binding.skipButton.isVisible = canSkip
            }
        }
        binding.skipButton.setOnClickListener { timerService.skipPreset() }
    }

    override fun onStart() {
        Log.d(DEBUG_TAG, "TimerFragment.onStart()")

        navigator().bindTimerService()
        navigator().setExitFragmentListener()

        super.onStart()
    }

    override fun onStop() {
        Log.d(DEBUG_TAG, "TimerFragment.onStop()")

        navigator().unbindTimerService()

        super.onStop()
    }

    override fun onDestroy() {
        Log.d(DEBUG_TAG, "TimerFragment.onDestroy()")

        navigator().disableBroadcast()
        navigator().setServiceConnection(null)

        super.onDestroy()
    }

    companion object {
        const val ARG_PRESET_LIST = "ARG_PRESET_LIST"

        fun newInstance(presets: List<Preset>) = TimerFragment().apply {
            arguments = bundleOf(
                ARG_PRESET_LIST to ArrayList(presets)
            )
        }
    }
}