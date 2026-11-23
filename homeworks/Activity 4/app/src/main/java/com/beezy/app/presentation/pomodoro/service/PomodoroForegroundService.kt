package com.beezy.app.presentation.pomodoro.service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.beezy.app.BeezyApplication
import com.beezy.app.MainActivity
import com.beezy.app.R

class PomodoroForegroundService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val modeTitle = intent.getStringExtra(EXTRA_MODE) ?: "Focus"
                val timeFormatted = intent.getStringExtra(EXTRA_TIME) ?: "25:00"
                startForeground(NOTIFICATION_ID, buildNotification(modeTitle, timeFormatted))
            }
            ACTION_UPDATE -> {
                val modeTitle = intent.getStringExtra(EXTRA_MODE) ?: "Focus"
                val timeFormatted = intent.getStringExtra(EXTRA_TIME) ?: "25:00"
                val notification = buildNotification(modeTitle, timeFormatted)
                val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager.notify(NOTIFICATION_ID, notification)
            }
            ACTION_STOP -> {
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    private fun buildNotification(mode: String, time: String): Notification {
        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            openAppIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, BeezyApplication.CHANNEL_POMODORO)
            .setContentTitle("🐝 Beezy $mode Session")
            .setContentText("Time remaining: $time")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()
    }

    companion object {
        const val NOTIFICATION_ID = 101
        const val ACTION_START = "com.beezy.app.POMODORO_START"
        const val ACTION_UPDATE = "com.beezy.app.POMODORO_UPDATE"
        const val ACTION_STOP = "com.beezy.app.POMODORO_STOP"
        const val EXTRA_MODE = "extra_mode"
        const val EXTRA_TIME = "extra_time"

        fun start(context: Context, mode: String, time: String) {
            val intent = Intent(context, PomodoroForegroundService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_MODE, mode)
                putExtra(EXTRA_TIME, time)
            }
            try {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
            } catch (e: Exception) {
                // Handle background start restriction gracefully
            }
        }

        fun update(context: Context, mode: String, time: String) {
            val intent = Intent(context, PomodoroForegroundService::class.java).apply {
                action = ACTION_UPDATE
                putExtra(EXTRA_MODE, mode)
                putExtra(EXTRA_TIME, time)
            }
            try {
                context.startService(intent)
            } catch (e: Exception) {
                // Handle gracefully
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, PomodoroForegroundService::class.java).apply {
                action = ACTION_STOP
            }
            try {
                context.startService(intent)
            } catch (e: Exception) {
                // Handle gracefully
            }
        }
    }
}
