package info.erulinman.litetimetracker

import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import info.erulinman.litetimetracker.data.entity.Preset
import info.erulinman.litetimetracker.databinding.ActivityMainBinding
import info.erulinman.litetimetracker.fragments.Selection
import info.erulinman.litetimetracker.fragments.CategoryListFragment
import info.erulinman.litetimetracker.fragments.TimerFragment
import info.erulinman.litetimetracker.fragments.dialogs.ExitFragment
import info.erulinman.litetimetracker.utilities.DEBUG_TAG

class MainActivity: AppCompatActivity(), Navigator {

    private val currentFragment: Fragment
        get() = supportFragmentManager.findFragmentById(R.id.mainFragmentContainer)!!

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(DEBUG_TAG, "MainActivity.broadcastReceiver.onReceive()")
            intent?.let { it ->
                if (it.action == TimerService.CLOSE)
                    needToCloseTimerFragment = true
            }
        }
    }

    private var serviceConnection: ServiceConnection? = null

    private var timerService: TimerService? = null

    private var bound = false

    private var needToCloseTimerFragment = false

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(DEBUG_TAG, "MainActivity.onCreate()")
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            val fragment = CategoryListFragment()
            supportFragmentManager.commit {
                add(R.id.mainFragmentContainer, fragment)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(DEBUG_TAG, "MainActivity.onStart(): $currentFragment")
        if (needToCloseTimerFragment && currentFragment is TimerFragment) {
            Log.d(DEBUG_TAG, "MainActivity.onStart(): close TimerFragment")
            supportFragmentManager.popBackStack()
        }
    }

    override fun onBackPressed() {
        Log.d(DEBUG_TAG, "MainActivity.onBackPressed()")
        when (currentFragment) {
            is Selection -> (currentFragment as Selection).run {
                if (hasSelection) {
                    cancelSelection()
                    return
                }
            }
            is TimerFragment -> {
                timerService?.let { timer ->
                    if (timer.state.value != TimerService.FINISHED) {
                        ExitFragment.show(supportFragmentManager)
                        return
                    }
                    timer.closeService()
                }
            }
        }
        super.onBackPressed()
    }

    /**
     * Implementation of navigator interface functions
     *
     * TODO(description)
     */

    override fun updateToolbar(title: String, actionIconRes: Int, action: () -> Unit) {
        binding.title.text = title
        binding.btnAction.setImageResource(actionIconRes)
        binding.btnAction.setOnClickListener { action() }
    }

    override fun setToolbarActionVisibility(visibility: Boolean) {
        binding.btnAction.isVisible = visibility
    }

    override fun updateTitle(title: String) {
        binding.title.text = title
    }

    override fun updateTitle(visibility: Boolean) {
        binding.title.isVisible = visibility
    }

    override fun bindTimerService() {
        if (needToCloseTimerFragment) return
        Log.d(DEBUG_TAG, "MainActivity.bindTimerService()")
        val intent = Intent(this, TimerService::class.java)
        startForegroundService(intent)
        serviceConnection?.let {
            bindService(intent, it, BIND_ABOVE_CLIENT)
        }
    }

    override fun unbindTimerService() {
        if (bound) {
            Log.d(DEBUG_TAG, "MainActivity.unbindTimerService()")
            serviceConnection?.let { unbindService(it) }
            bound = false
            timerService = null
        }
    }

    override fun enableBroadcast() {
        Log.d(DEBUG_TAG, "MainActivity.enableBroadcast(): $currentFragment")
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(broadcastReceiver, IntentFilter(TimerService.CLOSE))
    }

    override fun disableBroadcast() {
        Log.d(DEBUG_TAG, "MainActivity.disableBroadcast()")
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
        needToCloseTimerFragment = false
    }

    override fun setServiceConnection(presets: List<Preset>?) {
        Log.d(DEBUG_TAG, "MainActivity.setServiceConnection()")
        if (presets == null) {
            serviceConnection = null
            return
        }
        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                Log.d(DEBUG_TAG, "MainActivity.setServiceConnection().onServiceConnected()")
                timerService = (service as TimerService.LocalBinder).getService().apply {
                    loadPresets(presets)
                    if (currentFragment is TimerFragment) {
                        (currentFragment as TimerFragment).onBindService(this)
                    }
                    bound = true
                }
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                Log.d(DEBUG_TAG, "MainActivity.setServiceConnection().onServiceDisconnected()")
                if (currentFragment is TimerFragment) {
                    supportFragmentManager.popBackStack()
                }
                val exitFragment = supportFragmentManager.findFragmentByTag(ExitFragment.TAG)
                    ?: return
                if ((exitFragment as ExitFragment).isShowing) exitFragment.dismiss()

                unbindTimerService()
            }
        }
    }

    override fun showToast(stringRes: Int) = Toast.makeText(
        this,
        getString(stringRes),
        Toast.LENGTH_SHORT
    ).show()

    override fun setExitFragmentListener() {
        supportFragmentManager.setFragmentResultListener(
            ExitFragment.REQUEST_KEY, this
        ) { _, result ->
            if (result.getInt(ExitFragment.RESPONSE_KEY) == DialogInterface.BUTTON_POSITIVE) {
                timerService?.closeService()
            }
        }
    }
}