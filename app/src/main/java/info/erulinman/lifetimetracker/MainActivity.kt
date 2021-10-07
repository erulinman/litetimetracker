package info.erulinman.lifetimetracker

import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import info.erulinman.lifetimetracker.data.entity.Preset
import info.erulinman.lifetimetracker.databinding.ActivityMainBinding
import info.erulinman.lifetimetracker.fragments.Selection
import info.erulinman.lifetimetracker.fragments.CategoryListFragment
import info.erulinman.lifetimetracker.fragments.TimerFragment
import info.erulinman.lifetimetracker.utilities.Constants

class MainActivity: AppCompatActivity(), Navigator {
    private val currentFragment: Fragment
        get() = supportFragmentManager.findFragmentById(R.id.mainFragmentContainer)!!

    private var broadcastManager: LocalBroadcastManager? = null
    private var serviceConnection: ServiceConnection? = null
    private var timerService: TimerService? = null
    private var closeTimerFragment = false

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(Constants.DEBUG_TAG, "MainActivity.onCreate()")
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

    override fun onDestroy() {
        super.onDestroy()
        timerService?.closeService()
    }

    /**
     * Implementation of navigator interface functions
     *
     * TODO(description)
     */

    override fun onResume() {
        super.onResume()
        if (closeTimerFragment && currentFragment is TimerFragment) {
            supportFragmentManager.popBackStack()
            closeTimerFragment = false
        }
    }

    override fun updateAppBar(iconRes: Int, title: String, action: () -> Unit) {
        binding.appBarTitle.text = title
        binding.fab.setImageResource(iconRes)
        binding.fab.setOnClickListener {
            action()
        }
    }

    override fun updateAppBar(title: String) {
        binding.appBarTitle.text = title
    }

    override fun updateAppBar(
        iconRes: Int,
        titleIsVisible: Boolean,
        action: () -> Unit
    ) {
        binding.appBarTitle.visibility = if (titleIsVisible) View.VISIBLE else View.GONE
        binding.fab.setImageResource(iconRes)
        binding.fab.setOnClickListener {
            action()
        }
    }

    override fun bindTimerService() {
        Log.d(Constants.DEBUG_TAG, "MainActivity.bindTimerService()")
        val intent = Intent(this, TimerService::class.java)
        startForegroundService(intent)
        serviceConnection?.let {
            bindService(intent, it, BIND_ABOVE_CLIENT)
        }
    }

    override fun unbindTimerService() {
        Log.d(Constants.DEBUG_TAG, "MainActivity.unbindTimerService()")
        serviceConnection?.let { unbindService(it) }
    }

    override fun enableBroadcast() {
        Log.d(Constants.DEBUG_TAG, "MainActivity.enableBroadcast()")
        val broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == TimerService.CLOSE && currentFragment is TimerFragment) {
                    Log.d(Constants.DEBUG_TAG, "MainActivity.enableBroadcast().popBackStack()")
                    closeTimerFragment = true
                }
            }
        }
        broadcastManager = LocalBroadcastManager.getInstance(this).apply {
            registerReceiver(broadcastReceiver, IntentFilter(TimerService.CLOSE))
        }
    }

    override fun disableBroadcast() {
        Log.d(Constants.DEBUG_TAG, "MainActivity.disableBroadcast()")
        broadcastManager = null
    }

    override fun setServiceConnection(presets: List<Preset>) {
        Log.d(Constants.DEBUG_TAG, "MainActivity.setServiceConnection()")
        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                Log.d(Constants.DEBUG_TAG, "MainActivity.setServiceConnection().onServiceConnected()")
                timerService = (service as TimerService.LocalBinder).getService().apply {
                    loadPresets(presets)
                    if (currentFragment is TimerFragment) {
                        (currentFragment as TimerFragment).setObservers(this)
                    }
                }
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                Log.d(Constants.DEBUG_TAG, "MainActivity.setServiceConnection().onServiceDisconnected()")
                if (currentFragment is TimerFragment) {
                    Log.d(Constants.DEBUG_TAG, "")
                    supportFragmentManager.popBackStack()
                }
            }
        }
    }

    override fun getTimerService(): TimerService? {
        Log.d(Constants.DEBUG_TAG, "MainActivity.getTimerService()")
        return timerService
    }

    override fun onBackPressed() {
        Log.d(Constants.DEBUG_TAG, "MainActivity.onBackPressed()")
        when (currentFragment) {
            is Selection -> (currentFragment as Selection).run {
                if (hasSelection) {
                    cancelSelection()
                    return
                }
            }
            is TimerFragment -> (timerService?.closeService())
        }
        super.onBackPressed()
    }
}