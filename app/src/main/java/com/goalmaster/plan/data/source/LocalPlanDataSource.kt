package com.goalmaster.plan.data.source

import com.goalmaster.Result
import com.goalmaster.plan.data.entity.Plan
import com.goalmaster.plan.data.entity.PlanState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class LocalPlanDataSource(
    private val planDao: PlanDao, private val ioDispatcher: CoroutineDispatcher
    ): PlanDataSource {

    override fun observeCurrentPlan(): Flow<Plan> {
        return planDao.observeCurrentPlan()
    }

    override suspend fun savePlan(plan: Plan): Result<Unit> = withContext(ioDispatcher) {
        return@withContext try {
            planDao.insert(plan)
            Result.Success(Unit)
        } catch (exception: Exception) {
            Result.Error(exception)
        }
    }

    override suspend fun updateState(planId: Long, state: PlanState): Result<Unit>  = withContext(ioDispatcher) {
        return@withContext try {
            val plan = planDao.findById(planId) ?: throw Exception("Not found")
            plan.state = state
            planDao.insert(plan)
            Result.Success(Unit)
        } catch (exception: Exception) {
            Result.Error(exception)
        }
    }
}