package com.goalmaster.notification

import com.goalmaster.utils.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class LocalNotificationDataSource (private val dao: NotificationDao,
                                   private val ioDispatcher: CoroutineDispatcher
) : NotificationDataSource {

    override suspend fun saveNotification(notification: Notification)
    : Result<Notification> = withContext(ioDispatcher) {
        return@withContext try {
            dao.insert(notification)
            Result.Success(notification)
        } catch (exception: Exception) {
            Result.Error(exception)
        }
    }

    override suspend fun getNotification(id: Long): Result<Notification>
    = withContext(ioDispatcher) {
        return@withContext try {
            val notification = dao.findById(id) ?: throw Exception()
            Result.Success(notification)
        } catch (exception: Exception) {
            Result.Error(exception)
        }
    }

    override suspend fun deleteNotificationByType(type: NotificationType):
            Result<Unit>  = withContext(ioDispatcher) {
        return@withContext try {
            dao.deleteNotificationsByType(type)
            Result.Success(Unit)
        } catch (exception: Exception) {
            Result.Error(exception)
        }
    }
}