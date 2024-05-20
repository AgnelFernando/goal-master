package com.goalmaster.plan.data.source

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.goalmaster.database.BaseDao
import com.goalmaster.plan.data.entity.PlanTask
import com.goalmaster.plan.data.entity.PlanTaskStatus
import com.goalmaster.task.data.entity.TaskWithData
import kotlinx.coroutines.flow.Flow

@Dao
interface PlanTaskDao : BaseDao<PlanTask> {

    @Query("DELETE FROM PlanTask where taskId=:taskId AND planId=:planId")
    suspend fun deleteById(planId: Long, taskId: Long)

    @Transaction
    @Query("SELECT t.* FROM Plan as p INNER JOIN PlanTask " +
            "as pt ON p.id=pt.planId INNER JOIN Task as t " +
            "ON t.id=pt.taskId WHERE (p.state='CREATED' or " +
            "p.state='LOCKED') AND pt.status == :ptStatus")
    fun observeCurrentPlanTasks(ptStatus: PlanTaskStatus): Flow<List<TaskWithData>>

    @Transaction
    @Query("SELECT t.* FROM Plan as p INNER JOIN PlanTask " +
            "as pt ON p.id=pt.planId INNER JOIN Task as t " +
            "ON t.id=pt.taskId WHERE (p.state='CREATED' or " +
            "p.state='LOCKED')")
    fun observeCurrentPlanAllTasks(): Flow<List<TaskWithData>>

    @Query("UPDATE PlanTask SET status='FAILED' WHERE planId=:planId")
    suspend fun cancelPlannedTasksByPlanId(planId: Long)

    @Query("UPDATE PlanTask SET status=:status WHERE taskId=:taskId")
    suspend fun updatePlanTaskStatusByTaskId(taskId: Long, status: String)

    @Query("SELECT * From PlanTask as pt WHERE pt.planId =:planId AND pt.status='CREATED'")
    suspend fun getAllPlannedTaskByPlanId(planId: Long): List<PlanTask>
    @Query("UPDATE Task SET state='UNPLANNED' WHERE id in " +
            "(SELECT pt.taskId FROM PlanTask as pt WHERE pt.planId=:planId)")
    suspend fun updateTaskStatus(planId: Long)
}