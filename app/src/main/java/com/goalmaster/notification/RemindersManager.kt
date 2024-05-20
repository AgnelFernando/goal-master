package com.goalmaster.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar
import java.util.Locale

object RemindersManager {

    const val REMINDER_NOTIFICATION_REQUEST_CODE = 123
    fun startReminder(
        context: Context,
        data: NotificationData
    ) {

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent =
            Intent(context.applicationContext, AlarmReceiver::class.java).let { intent ->
                intent.action = "ACTION_VIEW_REVIEW"
                PendingIntent.getBroadcast(
                    context.applicationContext,
                    data.id,
                    intent,
                    PendingIntent.FLAG_MUTABLE
                )
            }

        val calendar = Calendar.getInstance(Locale.ENGLISH).apply {
            set(Calendar.MONTH, data.time.monthValue - 1)
            set(Calendar.DAY_OF_MONTH, data.time.dayOfMonth)
            set(Calendar.HOUR_OF_DAY, data.time.hour)
            set(Calendar.MINUTE, data.time.minute)
        }

        // If the trigger time you specify is in the past, this just return
        if (Calendar.getInstance(Locale.ENGLISH)
                .apply { add(Calendar.MINUTE, 1) }.timeInMillis - calendar.timeInMillis > 0
        ) {
            return
        }

        if (alarmManager.canScheduleExactAlarms()) {
            alarmManager.setAlarmClock(
                AlarmManager.AlarmClockInfo(calendar.timeInMillis, intent),
                intent
            )
        }
    }

    fun stopReminder(context: Context,
                     reminderId: Int = REMINDER_NOTIFICATION_REQUEST_CODE) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(
                context,
                reminderId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
        alarmManager.cancel(intent)
    }
}