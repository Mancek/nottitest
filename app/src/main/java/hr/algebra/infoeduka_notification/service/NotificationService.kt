package hr.algebra.infoeduka_notification.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import hr.algebra.infoeduka_notification.MainActivity
import hr.algebra.infoeduka_notification.NotificationApp.Companion.CHANNEL_ID
import hr.algebra.infoeduka_notification.R
import hr.algebra.infoeduka_notification.data.model.Notification
import hr.algebra.infoeduka_notification.worker.SaveNotificationWorker
import com.google.gson.Gson
import java.time.Instant
import kotlin.random.Random

@AndroidEntryPoint
class NotificationService : FirebaseMessagingService() {


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

        // Convert notification data map to JSON
        val gson = Gson()
        val dataJson = gson.toJson(notification.data)

        // Schedule work to save notification
        val workData = Data.Builder()
            .putString(SaveNotificationWorker.KEY_TITLE, notification.title)
            .putString(SaveNotificationWorker.KEY_MESSAGE, notification.message)
            .putLong(SaveNotificationWorker.KEY_TIMESTAMP, Instant.now().epochSecond)
            .putString(SaveNotificationWorker.KEY_DATA, dataJson)
            .build()

        val saveRequest = OneTimeWorkRequestBuilder<SaveNotificationWorker>()
            .setInputData(workData)
            .build()

        WorkManager.getInstance(this)
            .enqueue(saveRequest)

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
