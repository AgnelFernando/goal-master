package com.goalmaster.plan.data.source

import com.goalmaster.Result
import com.goalmaster.plan.data.entity.Plan
import com.goalmaster.plan.data.entity.PlanState
import com.goalmaster.plan.data.entity.PlanTask
import com.goalmaster.task.data.source.LocalTaskDataSource
import com.goalmaster.task.data.entity.TaskState
import com.goalmaster.task.data.entity.TaskWithData
import kotlinx.coroutines.flow.Flow

class DefaultPlanRepository(
    private val dataSource: PlanDataSource,
    private val taskDataSource: LocalTaskDataSource
) : PlanRepository {

    override fun observeCurrentPlan(): Flow<Plan> {
        return dataSource.observeCurrentPlan()
    }

    override suspend fun getCurrentPlan(): Result<Plan> {
        return try {
            return dataSource.getCurrentPlan()
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun savePlan(plan: Plan): Result<Unit> {
        return try {
            return dataSource.savePlan(plan)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun  updateState(planId: Long, state: PlanState): Result<Unit> {
        return try {
            dataSource.updateState(planId, state)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun savePlanTask(planTask: PlanTask): Result<Unit> {
        return try {
            dataSource.savePlanTask(planTask)
            taskDataSource.updateTaskState(planTask.taskId, TaskState.PLANNED)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deletePlannedTask(planId: Long, taskId: Long): Result<Unit> {
        return try {
            dataSource.deletePlannedTask(planId, taskId)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun observeCurrentPlanTasks(): Flow<List<TaskWithData>> {
        return dataSource.observeCurrentPlanTasks()
    }
}