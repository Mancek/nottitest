package hr.algebra.infoeduka_notification.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import hr.algebra.infoeduka_notification.MainActivity
import hr.algebra.infoeduka_notification.NotificationApp.Companion.CHANNEL_ID
import hr.algebra.infoeduka_notification.NotificationApp.Companion.SERVICE_CHANNEL_ID
import hr.algebra.infoeduka_notification.R
import hr.algebra.infoeduka_notification.data.dao.NotificationDao
import hr.algebra.infoeduka_notification.data.model.Notification
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
class NotificationService : FirebaseMessagingService() {

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    @Inject
    lateinit var notificationDao: NotificationDao

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "FCM Token: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        
        val notification = Notification(
            title = message.notification?.title ?: "",
            message = message.notification?.body ?: "",
            data = message.data
        )

        // Start foreground service to ensure operation completes
        val serviceNotification = NotificationCompat.Builder(this, SERVICE_CHANNEL_ID)
            .setContentTitle("Processing Notification")
            .setContentText("Saving notification data...")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        startForeground(FOREGROUND_SERVICE_ID, serviceNotification)

        // Save notification in background with reliable scope
        // Use supervisorScope to prevent child coroutine failures from cancelling the parent
        serviceScope.launch {
            supervisorScope {
                try {
                    withContext(Dispatchers.IO) {
                        notificationDao.insertNotification(notification)
                    }
                    Log.d(TAG, "Notification saved successfully")
                } catch (e: Exception) {
                    Log.e(TAG, "Error saving notification", e)
                } finally {
                    // Always stop foreground service after completion
                    withContext(Dispatchers.Main) {
                        stopForeground(STOP_FOREGROUND_REMOVE)
                    }
                }
            }
        }

        // Show notification to user
        showNotification(notification)
    }

    private fun showNotification(notification: Notification) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(notification.title)
            .setContentText(notification.message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(Random.nextInt(), notificationBuilder.build())
    }

    companion object {
        private const val TAG = "NotificationService"
        private const val FOREGROUND_SERVICE_ID = 123
    }
}
