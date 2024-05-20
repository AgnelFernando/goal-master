package com.goalmaster.task.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity
data class TaskTimeTracker(@PrimaryKey(autoGenerate = true) val id : Long = 0L,
                           val taskId: Long,
                           val startTime: LocalDateTime = LocalDateTime.now(),
                           var endTime: LocalDateTime? = null)