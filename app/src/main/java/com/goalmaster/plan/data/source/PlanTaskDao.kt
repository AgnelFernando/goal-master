package com.goalmaster.plan.data.source

import androidx.room.Dao
import androidx.room.Query
import com.goalmaster.database.BaseDao
import com.goalmaster.plan.data.entity.PlanTask
import com.goalmaster.task.data.entity.TaskWithData
import kotlinx.coroutines.flow.Flow

@Dao
interface PlanTaskDao : BaseDao<PlanTask> {

    @Query("DELETE FROM PlanTask where taskId=:taskId AND planId=:planId")
    suspend fun deleteById(planId: Long, taskId: Long)

    @Query("SELECT t.* FROM Plan as p INNER JOIN PlanTask " +
            "as pt ON p.id=pt.planId INNER JOIN Task as t " +
            "ON t.id=pt.taskId WHERE (p.state='CREATED' or " +
            "p.state='LOCKED') AND pt.status == 'OPENED'")
    fun observeCurrentPlanTasks(): Flow<List<TaskWithData>>
}