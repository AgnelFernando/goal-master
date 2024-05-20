package com.goalmaster.plan.data.source

import com.goalmaster.utils.Result
import com.goalmaster.plan.data.entity.Plan
import com.goalmaster.plan.data.entity.PlanState
import com.goalmaster.plan.data.entity.PlanTask
import com.goalmaster.plan.data.entity.PlanTaskStatus
import com.goalmaster.task.data.entity.TaskWithData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class LocalPlanDataSource(
    private val planDao: PlanDao,
    private val planTaskDao: PlanTaskDao,
    private val ioDispatcher: CoroutineDispatcher
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

    override suspend fun updateState(planId: Long, state: PlanState): Result<Unit> = withContext(ioDispatcher) {
        return@withContext try {
            if (state == PlanState.COMPLETED) {
                planTaskDao.updateTaskStatus(planId)
                planTaskDao.cancelPlannedTasksByPlanId(planId)

            }
            val plan = planDao.findById(planId) ?: throw Exception("Not found")
            plan.state = state
            planDao.insert(plan)
            Result.Success(Unit)
        } catch (exception: Exception) {
            Result.Error(exception)
        }
    }

    override suspend fun getCurrentPlan(): Result<Plan> = withContext(ioDispatcher) {
        return@withContext try {
            val plan = planDao.getCurrentPlan() ?: throw Exception("Not found")
            Result.Success(plan)
        } catch (exception: Exception) {
            Result.Error(exception)
        }
    }

    override suspend fun savePlanTask(planTask: PlanTask) = withContext(ioDispatcher) {
        return@withContext try {
            planTaskDao.insert(planTask)
            Result.Success(Unit)
        } catch (exception: Exception) {
            Result.Error(exception)
        }
    }

    override suspend fun deletePlannedTask(planId: Long, taskId: Long) = withContext(ioDispatcher) {
        return@withContext try {
            planTaskDao.deleteById(planId, taskId)
            Result.Success(Unit)
        } catch (exception: Exception) {
            Result.Error(exception)
        }
    }

    override fun observeCurrentPlanTasks(ptStatus: PlanTaskStatus): Flow<List<TaskWithData>> {
        return planTaskDao.observeCurrentPlanTasks(ptStatus)
    }

    override fun observeCurrentPlanAllTasks(): Flow<List<TaskWithData>> {
        return planTaskDao.observeCurrentPlanAllTasks()
    }
}