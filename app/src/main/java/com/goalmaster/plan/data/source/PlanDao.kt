package com.goalmaster.plan.data.source

import androidx.room.Dao
import androidx.room.Query
import com.goalmaster.database.BaseDao
import com.goalmaster.goal.data.entity.Goal
import com.goalmaster.plan.data.entity.Plan
import com.goalmaster.task.data.entity.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface PlanDao : BaseDao<Plan> {

    @Query("""SELECT * FROM `Plan` as p where p.state='CREATED' or p.state='LOCKED' """)
    fun observeCurrentPlan(): Flow<Plan>

    @Query("SELECT * FROM Plan WHERE id=:id")
    suspend fun findById(id: Long): Plan?

    @Query("""SELECT * FROM Task as t where t.goalId=:goalId order by created""")
    fun observeGoalTasks(goalId: Long): Flow<List<Task>>
    @Query("""SELECT * FROM `Plan` as p where p.state='CREATED'""")
    suspend fun getCurrentPlan(): Plan?
}