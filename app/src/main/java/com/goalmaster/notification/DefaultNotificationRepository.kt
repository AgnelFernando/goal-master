package com.goalmaster.notification

import com.goalmaster.utils.Result

class DefaultNotificationRepository(private val dataSource: NotificationDataSource):
    NotificationRepository {

    override suspend fun saveNotification(notification: Notification): Result<Notification> {
        return try {
            return dataSource.saveNotification(notification)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getNotification(id: Long): Result<Notification> {
        return try {
            return dataSource.getNotification(id)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteNotificationByType(type: NotificationType): Result<Unit> {
        return try {
            return dataSource.deleteNotificationByType(type)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}