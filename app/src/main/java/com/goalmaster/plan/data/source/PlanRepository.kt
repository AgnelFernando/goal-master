package com.goalmaster.plan.data.source

import com.goalmaster.plan.data.entity.Plan
import kotlinx.coroutines.flow.Flow
import com.goalmaster.Result
import com.goalmaster.plan.data.entity.PlanState

interface PlanRepository {

    fun observeCurrentPlan(): Flow<Plan>

    suspend fun savePlan(plan: Plan): Result<Unit>

    suspend fun updateState(planId: Long, state: PlanState): Result<Unit>

}