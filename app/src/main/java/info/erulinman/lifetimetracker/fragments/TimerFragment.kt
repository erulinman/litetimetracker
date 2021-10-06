package info.erulinman.lifetimetracker.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import info.erulinman.lifetimetracker.R
import info.erulinman.lifetimetracker.data.entity.Preset
import info.erulinman.lifetimetracker.databinding.FragmentTimerBinding
import info.erulinman.lifetimetracker.TimerService
import info.erulinman.lifetimetracker.utilities.Constants

class TimerFragment : Fragment() {
    private lateinit var binding: FragmentTimerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(Constants.DEBUG_TAG, "TimerFragment.onCreate()")
        super.onCreate(savedInstanceState)
        arguments?.getParcelableArrayList<Preset>(ARG_PRESET_LIST)?.let { presets ->
            navigator().apply {
                enableBroadcast()
                setServiceConnection(presets)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(Constants.DEBUG_TAG, "TimerFragment.onCreateView()")
        binding = FragmentTimerBinding.inflate(inflater, container, false)
        return binding.root
    }

    fun setObservers(timerService: TimerService) {
        Log.d(Constants.DEBUG_TAG, "TimerFragment.setObservers()")
        timerService.apply {
            presetName.observe(viewLifecycleOwner) { navigator().updateAppBar(it) }
            time.observe(viewLifecycleOwner) { binding.timer.text = it}
            state.observe(viewLifecycleOwner) { state -> when (state) {
                TimerService.INITIALIZED -> startTimer()
                TimerService.STOPPED -> {
                    navigator().updateAppBar(R.drawable.ic_play) {
                        startTimer()
                    }
                }
                TimerService.STARTED -> {
                    navigator().updateAppBar(R.drawable.ic_pause, true) {
                        stopTimer()
                    }
                }
                TimerService.FINISHED -> {
                    navigator().updateAppBar(R.drawable.ic_restart, false) {
                        restartPresets()
                    }
                }
            }}
            canSkip.observe(viewLifecycleOwner) { canSkip ->
                binding.nextButton.visibility = if (canSkip) View.VISIBLE else View.GONE
                binding.nextButton.setOnClickListener { skipPreset() }
            }
        }
    }

    override fun onStart() {
        Log.d(Constants.DEBUG_TAG, "TimerFragment.onStart()")
        super.onStart()
        navigator().bindTimerService()
    }

    override fun onStop() {
        Log.d(Constants.DEBUG_TAG, "TimerFragment.onStop()")
        super.onStop()
        navigator().unbindTimerService()
    }

    override fun onDestroy() {
        Log.d(Constants.DEBUG_TAG, "TimerFragment.onDestroy()")
        super.onDestroy()
        navigator().disableBroadcast()
    }

    companion object {
        const val ARG_PRESET_LIST = "ARG_PRESET_LIST"

        fun newInstance(presets: List<Preset>) = TimerFragment().apply {
            Log.d(Constants.DEBUG_TAG, "TimerFragment.newInstance()")
            arguments = bundleOf(
                ARG_PRESET_LIST to ArrayList(presets)
            )
        }
    }
}