package com.goalmaster.notification

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity
data class Notification(@PrimaryKey(autoGenerate = true) val id : Long = 0L,
                        val title: String, val description: String,
                        val time: LocalDateTime, val type: NotificationType)