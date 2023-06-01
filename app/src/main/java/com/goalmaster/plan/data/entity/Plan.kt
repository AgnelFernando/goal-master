package com.goalmaster.plan.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity
data class Plan(@PrimaryKey(autoGenerate = true) val id : Long = 0L,
                var startDate: LocalDateTime,
                var endDate: LocalDateTime,
                var state: PlanState = PlanState.CREATED)