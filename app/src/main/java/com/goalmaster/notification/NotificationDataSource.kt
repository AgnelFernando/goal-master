package com.goalmaster.notification

import com.goalmaster.utils.Result

interface NotificationDataSource {

    suspend fun saveNotification(notification: Notification): Result<Notification>

    suspend fun getNotification(id: Long): Result<Notification>

    suspend fun deleteNotificationByType(type: NotificationType): Result<Unit>
}
