package com.goalmaster.goal.data.source

import com.goalmaster.Result
import com.goalmaster.goal.data.entity.Goal
import com.goalmaster.goal.data.entity.GoalState
import com.goalmaster.goal.view.create.CreateGoalRequest
import com.goalmaster.task.data.entity.Task
import kotlinx.coroutines.flow.Flow

interface GoalDataSource {

    suspend fun createGoal(request: CreateGoalRequest): Result<Unit>

    fun observeActiveGoals(state: GoalState): Flow<List<Goal>>

    suspend fun updateCompletedUnits(id: Long, completed: Int): Result<Unit>

    suspend fun getGoal(goalId: Long): Result<Goal>

    fun observeGoalTasks(goalId: Long): Flow<List<Task>>

    fun observeGoal(goalId: Long): Flow<Goal>

    suspend fun saveGoal(goal: Goal): Result<Unit>

    suspend fun delete(id: Long): Result<Unit>
}