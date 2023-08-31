package com.goalmaster.plan.data.source

import com.goalmaster.plan.data.entity.PlanTaskStatus
import kotlinx.coroutines.CoroutineDispatcher

class LocalPlanTaskDataSource(private val planTaskDao: PlanTaskDao,
                              private val ioDispatcher: CoroutineDispatcher
                              ): PlanTaskDataSource {

    override suspend fun updateStatusByTaskId(taskId: Long, status: PlanTaskStatus) {
        planTaskDao.updatePlanTaskStatusByTaskId(taskId, status.name)
    }
}