package com.goalmaster.goal.data.entity

import androidx.room.Embedded

data class GoalWithTaskCount(
    @Embedded val goal: Goal,
    val taskCount: Int
)
