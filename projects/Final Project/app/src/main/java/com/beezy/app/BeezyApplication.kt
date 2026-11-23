package com.beezy.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.beezy.app.di.AppContainer
import com.beezy.app.di.DefaultAppContainer

class BeezyApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val pomodoroChannel = NotificationChannel(
                CHANNEL_POMODORO,
                getString(R.string.notification_channel_pomodoro),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.notification_channel_pomodoro_desc)
                setShowBadge(false)
            }

            val reviewChannel = NotificationChannel(
                CHANNEL_REVIEWS,
                getString(R.string.notification_channel_reviews),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = getString(R.string.notification_channel_reviews_desc)
                setShowBadge(true)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(pomodoroChannel)
            notificationManager?.createNotificationChannel(reviewChannel)
        }
    }

    companion object {
        const val CHANNEL_POMODORO = "beezy_pomodoro_channel"
        const val CHANNEL_REVIEWS = "beezy_reviews_channel"
    }
}
