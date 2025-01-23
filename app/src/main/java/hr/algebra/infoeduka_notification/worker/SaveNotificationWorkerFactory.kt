package hr.algebra.infoeduka_notification.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import hr.algebra.infoeduka_notification.data.dao.NotificationDao
import javax.inject.Inject

class SaveNotificationWorkerFactory @Inject constructor(
    private val notificationDao: NotificationDao
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            SaveNotificationWorker::class.java.name -> {
                SaveNotificationWorker(appContext, workerParameters, notificationDao)
            }
            else -> null
        }
    }
}
