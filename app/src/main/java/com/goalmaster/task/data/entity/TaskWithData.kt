package com.goalmaster.task.data.entity

import androidx.room.Embedded
import androidx.room.Relation
import com.goalmaster.goal.data.entity.Goal
import com.goalmaster.plan.data.entity.PlanTask

data class TaskWithData(
    @Embedded val task: Task,

    @Relation(
        parentColumn = "goalId",
        entity = Goal::class,
        entityColumn = "id"
    )
    val goal: Goal,

    @Relation(
        parentColumn = "id",
        entity = PlanTask::class,
        entityColumn = "taskId"
    )
    val planTask: PlanTask?,
)