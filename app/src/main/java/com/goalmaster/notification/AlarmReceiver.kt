package com.goalmaster.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.goalmaster.MainActivity
import com.goalmaster.R


class AlarmReceiver : BroadcastReceiver() {

    /**
     * sends notification when receives alarm
     * and then reschedule the reminder again
     * */
    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = ContextCompat.getSystemService(
            context, NotificationManager::class.java
        ) as NotificationManager

        notificationManager.sendReminderNotification(
            applicationContext = context,
            channelId = context.getString(R.string.reminders_notification_channel_id)
        )
    }
}

    fun NotificationManager.sendReminderNotification(
        applicationContext: Context,
        channelId: String,
    ) {
        val contentIntent = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 1, contentIntent, PendingIntent.FLAG_MUTABLE
        )
        val builder = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle(applicationContext.getString(R.string.title_notification_reminder))
            .setContentText(applicationContext.getString(R.string.description_notification_reminder))
            .setSmallIcon(R.mipmap.ic_launcher_foreground).setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(applicationContext.getString(R.string.description_notification_reminder))
            ).setContentIntent(pendingIntent).setAutoCancel(true)

        notify(NOTIFICATION_ID, builder.build())
    }

const val NOTIFICATION_ID = 1