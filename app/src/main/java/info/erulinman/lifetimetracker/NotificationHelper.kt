package info.erulinman.lifetimetracker

import android.app.*
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.media.MediaPlayer
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC
import info.erulinman.lifetimetracker.utilities.ActionIntent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(context: Context) : ContextWrapper(context) {
    private val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    private val contentIntent = PendingIntent.getActivity(
        this,
        0,
        Intent(context, MainActivity::class.java),
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )
    private val notification = NotificationCompat.Builder(context, CHANNEL_ID)
        .setCategory(NotificationCompat.CATEGORY_PROGRESS)
        .setSmallIcon(R.drawable.ic_timer)
        .setVisibility(VISIBILITY_PUBLIC)
        .setOngoing(true)
        .setShowWhen(false)
        .setContentIntent(contentIntent)
    private val mediaPlayer = MediaPlayer.create(this, R.raw.sound)

    init {
        manager.createNotificationChannel(initChannel())
    }

    private fun initChannel(): NotificationChannel {
        val name = getString(R.string.notification_channel_name)
        val importance = NotificationManager.IMPORTANCE_LOW
        return NotificationChannel(CHANNEL_ID, name, importance)
    }

    fun getStartedNotificationBuilder(): NotificationCompat.Builder = notification
        .clearActions()
        .addAction(buildStopAction(this))
        //.addAction(buildSkipAction(this))
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
                //.addAction(buildSkipAction(this))
                .addAction(buildCloseAction(this))
                .build()
        )
    }

    fun notifyWhenFinished() {
        manager.notify(
            NOTIFICATION_ID,
            notification
                .clearActions()
                .setContentText(getString(R.string.text_view_timer_finished))
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
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val title = getString(R.string.notification_action_title_stop)
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
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val title = getString(R.string.notification_action_title_start)
        return NotificationCompat.Action.Builder(
            R.drawable.ic_play,
            title,
            pendingIntent
        ).build()
    }

    private fun buildRestartAction(context: Context): NotificationCompat.Action {
        val pendingIntent = PendingIntent.getService(
            context,
            ACTION_RESTART_ALL_ID,
            ActionIntent(context, TimerService::class.java, TimerService.RESTART_ALL),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val title = getString(R.string.notification_action_title_restart)
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
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val title = getString(R.string.notification_action_title_skip)
        return NotificationCompat.Action.Builder(
            R.drawable.ic_skip,
            title,
            pendingIntent
        ).build()
    }

    private fun buildCloseAction(context: Context): NotificationCompat.Action {
        val pendingIntent = PendingIntent.getService(
            context,
            ACTION_CLOSE_ID,
            ActionIntent(context, TimerService::class.java, TimerService.CLOSE),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val title = getString(R.string.notification_action_title_close)
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
        private const val ACTION_RESTART_ALL_ID = 53
        private const val ACTION_SKIP_ID = 54
        private const val ACTION_CLOSE_ID = 55

        const val NOTIFICATION_ID = 50
        const val CHANNEL_ID = "info.erulinman.lifetimetracker.CHANNEL_ID"
    }
}