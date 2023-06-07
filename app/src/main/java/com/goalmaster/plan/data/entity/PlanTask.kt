package com.goalmaster.plan.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import java.time.LocalDateTime
import java.util.*

@Entity(primaryKeys = ["planId", "taskId"])
data class PlanTask(val planId : Long,
                    val taskId: Long,
                    var eventTime: LocalDateTime,
                    var durationInMinutes: Int,
                    var eventId: Long? = 0,
                    var status: PlanTaskStatus = PlanTaskStatus.OPENED
                )