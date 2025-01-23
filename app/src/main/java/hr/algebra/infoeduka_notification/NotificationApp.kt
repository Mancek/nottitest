package hr.algebra.infoeduka_notification

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import hr.algebra.infoeduka_notification.worker.SaveNotificationWorkerFactory
import javax.inject.Inject

@HiltAndroidApp
class NotificationApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: SaveNotificationWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

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

            // Service notification channel
            val serviceChannel = NotificationChannel(
                SERVICE_CHANNEL_ID,
                "Background Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Background notification processing"
                setShowBadge(false)
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            notificationManager.createNotificationChannel(serviceChannel)
        }
    }
}
