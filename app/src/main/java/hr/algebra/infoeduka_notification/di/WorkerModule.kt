package hr.algebra.infoeduka_notification.di

import android.content.Context
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import hr.algebra.infoeduka_notification.data.dao.NotificationDao
import hr.algebra.infoeduka_notification.worker.SaveNotificationWorkerFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WorkerModule {
    
    @Provides
    @Singleton
    fun provideWorkManager(
        @ApplicationContext context: Context
    ): WorkManager = WorkManager.getInstance(context)

    @Provides
    @Singleton
    fun provideSaveNotificationWorkerFactory(
        notificationDao: NotificationDao
    ): SaveNotificationWorkerFactory = SaveNotificationWorkerFactory(notificationDao)
}
