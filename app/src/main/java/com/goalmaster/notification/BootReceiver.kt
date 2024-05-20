package com.goalmaster.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.time.LocalDateTime


class BootReceiver : BroadcastReceiver() {
    /*
    * restart reminders alarms when user's device reboots
    * */
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            RemindersManager.startReminder(context, NotificationData(1, "","", LocalDateTime.now()))
        }
    }
}