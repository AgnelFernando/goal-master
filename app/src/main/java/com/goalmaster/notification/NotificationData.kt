package com.goalmaster.notification

import java.time.LocalDateTime

data class NotificationData(val id: Int, val title: String, val description: String,
                            val time: LocalDateTime)