package hr.algebra.infoeduka_notification

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NotificationApp : Application() {

    companion object {
        const val CHANNEL_ID = "fcm_notification_channel"
        const val SERVICE_CHANNEL_ID = "fcm_service_channel"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // User notification channel
            val channel = NotificationChannel(
                CHANNEL_ID,
                "FCM Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Receives Firebase Cloud Messages"
                enableVibration(true)
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
