package com.goalmaster.plan.data.source

import com.goalmaster.Result
import com.goalmaster.plan.data.entity.Plan
import com.goalmaster.plan.data.entity.PlanState
import kotlinx.coroutines.flow.Flow

class DefaultPlanRepository(
    private val dataSource: PlanDataSource
) : PlanRepository {

    override fun observeCurrentPlan(): Flow<Plan> {
        return dataSource.observeCurrentPlan()
    }

    override suspend fun savePlan(plan: Plan): Result<Unit> {
        return try {
            return dataSource.savePlan(plan)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateState(planId: Long, state: PlanState): Result<Unit> {
        return try {
            dataSource.updateState(planId, state)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}