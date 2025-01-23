package hr.algebra.infoeduka_notification.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import hr.algebra.infoeduka_notification.data.model.Notification
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Insert
    suspend fun insertNotification(notification: Notification)

    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<Notification>>

    @Query("DELETE FROM notifications")
    suspend fun deleteAllNotifications()
}
