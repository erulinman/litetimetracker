package info.erulinman.lifetimetracker

import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import info.erulinman.lifetimetracker.data.entity.Preset
import info.erulinman.lifetimetracker.databinding.ActivityMainBinding
import info.erulinman.lifetimetracker.fragments.Selection
import info.erulinman.lifetimetracker.fragments.CategoryListFragment
import info.erulinman.lifetimetracker.fragments.TimerFragment
import info.erulinman.lifetimetracker.fragments.dialogs.ExitFragment
import info.erulinman.lifetimetracker.utilities.Constants

class MainActivity: AppCompatActivity(), Navigator {
    private val currentFragment: Fragment
        get() = supportFragmentManager.findFragmentById(R.id.mainFragmentContainer)!!

    private var broadcastManager: LocalBroadcastManager? = null
    private var serviceConnection: ServiceConnection? = null
    private var timerService: TimerService? = null
    private var bound = false
    private var needToCloseTimerFragment = false

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

    /**
     * Implementation of navigator interface functions
     *
     * TODO(description)
     */

    override fun onStart() {
        super.onStart()
        Log.d(Constants.DEBUG_TAG, "MainActivity.onStart(): $currentFragment")
        if (needToCloseTimerFragment && currentFragment is TimerFragment) {
            Log.d(Constants.DEBUG_TAG, "MainActivity.onStart(): close TimerFragment")
            supportFragmentManager.popBackStack()
        }
    }

    override fun updateAppBar(iconRes: Int, title: String, action: () -> Unit) {
        binding.apply {
            appBarTitle.text = title
            fab.setImageResource(iconRes)
            fab.setOnClickListener {
                action()
            }
        }
    }

    override fun updateAppBarTitle(title: String) {
        binding.appBarTitle.text = title
    }

    override fun updateAppBarTitle(visibility: Boolean) {
        binding.appBarTitle.visibility = if (visibility) View.VISIBLE else View.GONE
    }

    override fun updateFabOnAppBar(iconRes: Int, action: () -> Unit) {
        binding.fab.setImageResource(iconRes)
        binding.fab.setOnClickListener {
            action()
        }
    }

    override fun setOnClickListenerToAppBarTitle(actionOnClick: (() -> Unit)?) {
        binding.appBarTitle.setOnClickListener(
            actionOnClick?.let {
                View.OnClickListener { it() }
            }
        )
    }

    override fun bindTimerService() {
        if (needToCloseTimerFragment) return
        Log.d(Constants.DEBUG_TAG, "MainActivity.bindTimerService()")
        val intent = Intent(this, TimerService::class.java)
        startForegroundService(intent)
        serviceConnection?.let {
            bindService(intent, it, BIND_ABOVE_CLIENT)
        }
    }

    override fun unbindTimerService() {
        if (bound) {
            Log.d(Constants.DEBUG_TAG, "MainActivity.unbindTimerService()")
            serviceConnection?.let { unbindService(it) }
            bound = false
        }
    }

    override fun enableBroadcast() {
        Log.d(Constants.DEBUG_TAG, "MainActivity.enableBroadcast(): $currentFragment")
        val broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                Log.d(Constants.DEBUG_TAG, "MainActivity.broadcastReceiver.onReceive()")
                intent?.let {
                    if (it.action == TimerService.CLOSE) {
                        needToCloseTimerFragment = true
                        if (ExitFragment.isShowing) ExitFragment.close()
                    }
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
        needToCloseTimerFragment = false
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
                bound = true
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                Log.d(Constants.DEBUG_TAG, "MainActivity.setServiceConnection().onServiceDisconnected()")
                if (currentFragment is TimerFragment) {
                    Log.d(Constants.DEBUG_TAG, "")
                    supportFragmentManager.popBackStack()
                }
                bound = false
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