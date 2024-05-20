package com.goalmaster.plan.data.entity

import androidx.room.Entity
import java.time.LocalDateTime
import java.time.ZoneId

@Entity(primaryKeys = ["planId", "taskId"])
data class PlanTask(val planId : Long,
                    val taskId: Long,
                    var eventTime: LocalDateTime,
                    var durationInMinutes: Int,
                    var eventId: Long? = 0,
                    var status: PlanTaskStatus = PlanTaskStatus.OPENED
                ) {

    fun getTaskEventStartTime(): Long {
        return eventTime.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000
    }

    fun getTaskEventEndTime(): Long {
        return eventTime.plusMinutes(durationInMinutes.toLong())
            .atZone(ZoneId.systemDefault()).toEpochSecond() * 1000
    }
}