package info.erulinman.lifetimetracker.model

import android.app.*
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.media.MediaPlayer
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC
import info.erulinman.lifetimetracker.R
import info.erulinman.lifetimetracker.ui.TimerActivity
import info.erulinman.lifetimetracker.utilities.ActionIntent

class NotificationHelper(context: Context) : ContextWrapper(context) {
    private val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    private val notification = NotificationCompat.Builder(context, CHANNEL_ID)
        .setCategory(NotificationCompat.CATEGORY_PROGRESS)
        .setSmallIcon(R.drawable.ic_timer)
        .setVisibility(VISIBILITY_PUBLIC)
        .setOngoing(true)
        .setShowWhen(false)
        //.setContentIntent(getResultPendingIntent())
    private val mediaPlayer = MediaPlayer.create(this, R.raw.sound)

    init {
        manager.createNotificationChannel(initChannel())
    }

    private fun initChannel(): NotificationChannel {
        val name = getString(R.string.notification_channel_name)
        val description = getString(R.string.notification_channel_description)
        val importance = NotificationManager.IMPORTANCE_LOW
        return NotificationChannel(CHANNEL_ID, name, importance).apply {
            setDescription(description)
        }
    }

    /**
     * Broken because cant YET rebuild activity stack without data
     * */
    private fun getResultPendingIntent(): PendingIntent {
        val resultIntent = Intent(this, TimerActivity::class.java)
        return TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(resultIntent)
            getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }

    fun getStartedNotificationBuilder(): NotificationCompat.Builder = notification
        .clearActions()
        .addAction(buildStopAction(this))
        .addAction(buildSkipAction(this))
        .addAction(buildCloseAction(this))

    fun updateStartedNotification(time: String) {
        manager.notify(
            NOTIFICATION_ID,
            getStartedNotificationBuilder()
                .setContentText(time)
                .build()
        )
    }

    fun notifyWhenStopped(time: String) {
        manager.notify(
            NOTIFICATION_ID,
            notification
                .clearActions()
                .setContentText(time)
                .addAction(buildStartAction(this))
                .addAction(buildSkipAction(this))
                .addAction(buildCloseAction(this))
                .build()
        )
    }

    fun notifyWhenFinished() {
        manager.notify(
            NOTIFICATION_ID,
            notification
                .clearActions()
                .setContentText(getString(R.string.time_value_on_finish))
                .addAction(buildRestartAction(this))
                .addAction(buildCloseAction(this))
                .build()
        )
    }

    private fun buildStopAction(context: Context): NotificationCompat.Action {
        val pendingIntent = PendingIntent.getService(
            context,
            ACTION_STOP_ID,
            ActionIntent(context, TimerService::class.java, TimerService.STOP),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val title = getString(R.string.notification_action_stop_title)
        return NotificationCompat.Action.Builder(
            R.drawable.ic_pause,
            title,
            pendingIntent
        ).build()
    }

    private fun buildStartAction(context: Context): NotificationCompat.Action {
        val pendingIntent = PendingIntent.getService(
            context,
            ACTION_START_ID,
            ActionIntent(context, TimerService::class.java, TimerService.START),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val title = getString(R.string.notification_action_start_title)
        return NotificationCompat.Action.Builder(
            R.drawable.ic_play,
            title,
            pendingIntent
        ).build()
    }

    private fun buildRestartAction(context: Context): NotificationCompat.Action {
        val pendingIntent = PendingIntent.getService(
            context,
            ACTION_RESTART_ID,
            ActionIntent(context, TimerService::class.java, TimerService.RESTART),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val title = getString(R.string.notification_action_restart_title)
        return NotificationCompat.Action.Builder(
            R.drawable.ic_restart,
            title,
            pendingIntent
        ).build()
    }

    private fun buildSkipAction(context: Context): NotificationCompat.Action {
        val pendingIntent = PendingIntent.getService(
            context,
            ACTION_SKIP_ID,
            ActionIntent(context, TimerService::class.java, TimerService.SKIP),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val title = getString(R.string.notification_action_skip_title)
        return NotificationCompat.Action.Builder(
            R.drawable.ic_next,
            title,
            pendingIntent
        ).build()
    }

    private fun buildCloseAction(context: Context): NotificationCompat.Action {
        val pendingIntent = PendingIntent.getService(
            context,
            ACTION_CLOSE_ID,
            ActionIntent(context, TimerService::class.java, TimerService.CLOSE),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val title = getString(R.string.notification_action_close_title)
        return NotificationCompat.Action.Builder(
            R.drawable.ic_close,
            title,
            pendingIntent
        ).build()
    }

    fun playSound() {
        mediaPlayer.start()
    }

    companion object {
        private const val ACTION_START_ID = 51
        private const val ACTION_STOP_ID = 52
        private const val ACTION_RESTART_ID = 53
        private const val ACTION_SKIP_ID = 54
        private const val ACTION_CLOSE_ID = 55
        const val NOTIFICATION_ID = 50
        const val CHANNEL_ID = "info.erulinman.lifetimetracker.CHANNEL_ID"
    }
}