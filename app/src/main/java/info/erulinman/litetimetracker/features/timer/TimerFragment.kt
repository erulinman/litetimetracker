package info.erulinman.litetimetracker.features.timer

import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import info.erulinman.litetimetracker.BaseFragment
import info.erulinman.litetimetracker.R
import info.erulinman.litetimetracker.data.entity.Preset
import info.erulinman.litetimetracker.databinding.FragmentTimerBinding

class TimerFragment : BaseFragment<FragmentTimerBinding>(R.layout.fragment_timer) {

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == TimerService.CLOSE)
                needToCloseTimerFragment = true
        }
    }

    private var serviceConnection: ServiceConnection? = null

    private var bound = false

    private var needToCloseTimerFragment = false

    private var _service: TimerService? = null
    private val service: TimerService
        get() {
            checkNotNull(_service)
            return _service as TimerService
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().getParcelableArrayList<Preset>(ARG_PRESET_LIST)?.let { presets ->
            setServiceConnection(presets)
        }
        enableBroadcast()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentTimerBinding.bind(view)
        toolbar.setToolbarActionVisibility(false)
    }

    override fun onStart() {
        bindTimerService()
        setExitFragmentListener()
        if (needToCloseTimerFragment) {
            parentFragmentManager.popBackStack()
        }
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
        unbindTimerService()
        _service = null
    }

    override fun onDestroy() {
        super.onDestroy()
        disableBroadcast()
        setServiceConnection(null)
    }

    override fun onBackPressed(): Boolean {
        if (service.state.value != TimerService.FINISHED) {
            ExitFragment.show(parentFragmentManager)
            return false
        }
        service.closeService()
        return true
    }

    private fun observeTimerService() {
        binding.fabSkip.setOnClickListener { service.skipPreset() }
        binding.fabRestartCurrent.setOnClickListener {
            service.restartCurrentPreset()
        }
        service.presetName.observe(viewLifecycleOwner) { presetName ->
            toolbar.updateTitle(presetName)
        }
        service.time.observe(viewLifecycleOwner) { time ->
            binding.timer.text = time
        }
        service.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                TimerService.INITIALIZED -> service.startTimer()
                TimerService.STOPPED -> {
                    binding.fabMain.setImageResource(R.drawable.ic_play)
                    binding.fabMain.setOnClickListener { service.startTimer() }
                    binding.fabRestartCurrent.isVisible = true
                    toolbar.updateTitle(true)
                }
                TimerService.STARTED -> {
                    binding.fabMain.setImageResource(R.drawable.ic_pause)
                    binding.fabMain.setOnClickListener { service.stopTimer() }
                    binding.fabRestartCurrent.isVisible = true
                    toolbar.updateTitle(true)
                }
                TimerService.FINISHED -> {
                    binding.fabMain.setImageResource(R.drawable.ic_restart)
                    binding.fabMain.setOnClickListener { service.restartPresets() }
                    binding.fabRestartCurrent.isVisible = false
                    toolbar.updateTitle(false)
                }
            }
        }
        service.canSkip.observe(viewLifecycleOwner) { canSkip ->
            binding.fabSkip.isVisible = canSkip
        }
    }

    private fun setExitFragmentListener() {
        parentFragmentManager.setFragmentResultListener(
            ExitFragment.REQUEST_KEY, this
        ) { _, result ->
            if (result.getInt(ExitFragment.RESPONSE_KEY) == DialogInterface.BUTTON_POSITIVE) {
                service.closeService()
            }
        }
    }

    private fun bindTimerService() {
        if (needToCloseTimerFragment) return
        val intent = Intent(requireContext(), TimerService::class.java)
        requireActivity().startForegroundService(intent)
        serviceConnection?.let {
            requireActivity().bindService(intent, it, AppCompatActivity.BIND_ABOVE_CLIENT)
        }
    }

    private fun unbindTimerService() {
        if (bound) {
            serviceConnection?.let { requireActivity().unbindService(it) }
            bound = false
            _service = null
        }
    }

    private fun enableBroadcast() {
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(broadcastReceiver, IntentFilter(TimerService.CLOSE))
    }

    private fun disableBroadcast() {
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(broadcastReceiver)
        needToCloseTimerFragment = false
    }

    private fun setServiceConnection(presets: List<Preset>?) {
        if (presets == null) {
            serviceConnection = null
            return
        }
        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
                _service = (binder as TimerService.LocalBinder).getService()
                service.loadPresets(presets)
                observeTimerService()
                bound = true
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                parentFragmentManager.popBackStack()

                val exitFragment = parentFragmentManager
                    .findFragmentByTag(ExitFragment.TAG) as? ExitFragment ?: return
                if (exitFragment.isShowing) exitFragment.dismiss()

                unbindTimerService()
            }
        }
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