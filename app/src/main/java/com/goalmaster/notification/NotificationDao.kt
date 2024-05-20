package com.goalmaster.notification

import androidx.room.Dao
import androidx.room.Query
import com.goalmaster.database.BaseDao

@Dao
interface NotificationDao : BaseDao<Notification> {

    @Query("SELECT * FROM Notification WHERE id=:id")
    suspend fun findById(id: Long): Notification?

    @Query("DELETE FROM Notification WHERE type=:type")
    suspend fun deleteNotificationsByType(type: NotificationType)
}