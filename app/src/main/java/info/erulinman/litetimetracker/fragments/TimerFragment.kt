package info.erulinman.litetimetracker.fragments

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import info.erulinman.litetimetracker.R
import info.erulinman.litetimetracker.TimerService
import info.erulinman.litetimetracker.data.entity.Preset
import info.erulinman.litetimetracker.databinding.FragmentTimerBinding

class TimerFragment : Fragment(R.layout.fragment_timer), TimerService.OnBindService {

    private var _binding: FragmentTimerBinding? = null
    private val binding: FragmentTimerBinding
        get() {
            checkNotNull(_binding)
            return _binding as FragmentTimerBinding
        }

    private var _service: TimerService? = null
    private val service: TimerService
        get() {
            checkNotNull(_service)
            return _service as TimerService
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        requireArguments().getParcelableArrayList<Preset>(ARG_PRESET_LIST)?.let { presets ->
            navigator().setServiceConnection(presets)
        }

        navigator().enableBroadcast()

        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentTimerBinding.bind(view)

        navigator().setToolbarActionVisibility(false)

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onBindService(service: TimerService) {
        _service = service
        observeTimerService()
    }

    private fun observeTimerService() {
        binding.fabSkip.setOnClickListener { service.skipPreset() }
        binding.fabRestartCurrent.setOnClickListener {
            service.restartCurrentPreset()
        }
        service.presetName.observe(viewLifecycleOwner) { presetName ->
            navigator().updateTitle(presetName)
        }
        service.time.observe(viewLifecycleOwner) { time ->
            binding.timer.text = time
        }
        service.state.observe(viewLifecycleOwner) { state -> when (state) {
            TimerService.INITIALIZED -> service.startTimer()
            TimerService.STOPPED -> {
                binding.fabMain.setImageResource(R.drawable.ic_play)
                binding.fabMain.setOnClickListener { service.startTimer() }
                binding.fabRestartCurrent.isVisible = true
                navigator().updateTitle(true)
            }
            TimerService.STARTED -> {
                binding.fabMain.setImageResource(R.drawable.ic_pause)
                binding.fabMain.setOnClickListener { service.stopTimer() }
                binding.fabRestartCurrent.isVisible = true
                navigator().updateTitle(true)
            }
            TimerService.FINISHED -> {
                binding.fabMain.setImageResource(R.drawable.ic_restart)
                binding.fabMain.setOnClickListener { service.restartPresets() }
                binding.fabRestartCurrent.isVisible = false
                navigator().updateTitle(false)
            }
        }}
        service.canSkip.observe(viewLifecycleOwner) { canSkip ->
            binding.fabSkip.isVisible = canSkip
        }
    }

    override fun onStart() {
        navigator().bindTimerService()
        navigator().setExitFragmentListener()

        super.onStart()
    }

    override fun onStop() {
        navigator().unbindTimerService()

        _service = null

        super.onStop()
    }

    override fun onDestroy() {
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