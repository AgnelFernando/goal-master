package com.goalmaster.plan.data.source

import com.goalmaster.Result
import com.goalmaster.plan.data.entity.Plan
import com.goalmaster.plan.data.entity.PlanState
import kotlinx.coroutines.flow.Flow

interface PlanDataSource {

    fun observeCurrentPlan(): Flow<Plan>

    suspend fun savePlan(plan: Plan): Result<Unit>

    suspend fun updateState(planId: Long, state: PlanState): Result<Unit>
}