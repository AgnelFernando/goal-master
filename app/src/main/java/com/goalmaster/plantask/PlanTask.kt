package com.goalmaster.plantask

import androidx.room.Entity
import java.util.*

@Entity(primaryKeys = ["planId", "taskId"])
data class PlanTask(val planId : Long,
                    val taskId: Long,
                    var durationInMinutes: Int,
                    var eventTime: Date,
                    var status: PlanTaskStatus = PlanTaskStatus.OPENED
                )