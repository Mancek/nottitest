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
import hr.algebra.infoeduka_notification.R
import hr.algebra.infoeduka_notification.data.dao.NotificationDao
import hr.algebra.infoeduka_notification.data.model.Notification
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
class NotificationService : FirebaseMessagingService() {

    @Inject
    lateinit var notificationDao: NotificationDao

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "FCM Token: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d(TAG, "Message received")
        
        val notification = Notification(
            title = message.notification?.title ?: "",
            message = message.notification?.body ?: "",
            data = message.data,
            timestamp = Instant.now().epochSecond
        )

        // Save notification using GlobalScope
        GlobalScope.launch {
            try {
                notificationDao.insertNotification(notification)
                Log.d(TAG, "Notification saved successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Error saving notification", e)
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
    }
}
