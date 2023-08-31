package com.goalmaster.goal.data.source

import com.goalmaster.Result
import com.goalmaster.goal.data.entity.Goal
import com.goalmaster.goal.data.entity.GoalState
import com.goalmaster.goal.view.create.CreateGoalRequest
import com.goalmaster.task.data.entity.Task
import kotlinx.coroutines.flow.Flow

class DefaultGoalRepository(
    private val dataSource: LocalGoalDataSource
) : GoalRepository {

    override suspend fun createGoal(
        request: CreateGoalRequest
    ): Result<Unit> {
        return try {
            return dataSource.createGoal(request)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun observeActiveGoals(state: GoalState): Flow<List<Goal>> {
        return dataSource.observeActiveGoals(state)
    }

    override suspend fun updateGoalProgress(id: Long, completed: Int): Result<Unit> {
        return try {
            return dataSource.updateCompletedUnits(id, completed)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getGoal(goalId: Long): Result<Goal> {
        return try {
            dataSource.getGoal(goalId)
        } catch (ex: Exception) {
            Result.Error(ex)
        }
    }

    override fun observeGoalTasks(goalId: Long): Flow<List<Task>> {
        return dataSource.observeGoalTasks(goalId)
    }

    override fun observeGoal(goalId: Long): Flow<Goal> {
        return dataSource.observeGoal(goalId)
    }

    override suspend fun saveGoal(goal: Goal): Result<Unit> {
        return try {
            return dataSource.saveGoal(goal)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteGoal(id: Long): Result<Unit> {
        return try {
            return dataSource.delete(id)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}