package hr.algebra.infoeduka_notification.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import hr.algebra.infoeduka_notification.data.dao.NotificationDao
import hr.algebra.infoeduka_notification.data.model.Notification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class SaveNotificationWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val notificationDao: NotificationDao
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val title = inputData.getString(KEY_TITLE) ?: ""
            val message = inputData.getString(KEY_MESSAGE) ?: ""
            val timestamp = inputData.getLong(KEY_TIMESTAMP, 0)
            val dataJson = inputData.getString(KEY_DATA) ?: "{}"
            
            val gson = Gson()
            val typeToken = object : TypeToken<Map<String, String>>() {}.type
            val data = gson.fromJson<Map<String, String>>(dataJson, typeToken)
            
            val notification = Notification(
                title = title,
                message = message,
                timestamp = timestamp,
                data = data
            )

            notificationDao.insertNotification(notification)
            Log.d(TAG, "Notification saved successfully")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error saving notification", e)
            Result.failure()
        }
    }

    companion object {
        private const val TAG = "SaveNotificationWorker"
        const val KEY_TITLE = "title"
        const val KEY_MESSAGE = "message"
        const val KEY_TIMESTAMP = "timestamp"
        const val KEY_DATA = "data"
    }
}
