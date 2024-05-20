package com.goalmaster.plan.data.source

import com.goalmaster.plan.data.entity.Plan
import kotlinx.coroutines.flow.Flow
import com.goalmaster.utils.Result
import com.goalmaster.plan.data.entity.PlanState
import com.goalmaster.plan.data.entity.PlanTask
import com.goalmaster.plan.data.entity.PlanTaskStatus
import com.goalmaster.task.data.entity.TaskWithData

interface PlanRepository {

    fun observeCurrentPlan(): Flow<Plan>

    suspend fun getCurrentPlan(): Result<Plan>

    suspend fun savePlan(plan: Plan): Result<Unit>

    suspend fun updateState(planId: Long, state: PlanState): Result<Unit>
    suspend fun savePlanTask(planTask: PlanTask): Result<Unit>
    suspend fun deletePlannedTask(planId: Long, taskId: Long): Result<Unit>
    fun observeCurrentPlanTasks(ptStatus: PlanTaskStatus = PlanTaskStatus.OPENED): Flow<List<TaskWithData>>

    fun observeCurrentPlanAllTasks(): Flow<List<TaskWithData>>
}