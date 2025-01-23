package hr.algebra.infoeduka_notification.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "notifications")
data class Notification(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val message: String,
    val timestamp: Long = Instant.now().epochSecond,
    val data: Map<String, String> = emptyMap()
)
