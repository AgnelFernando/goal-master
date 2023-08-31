package com.goalmaster.plan.data.source

import com.goalmaster.plan.data.entity.PlanTaskStatus

interface PlanTaskDataSource {

    suspend fun updateStatusByTaskId(taskId: Long, status: PlanTaskStatus)

}