package com.goalmaster.task.data.source

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.goalmaster.database.BaseDao
import com.goalmaster.task.data.entity.Task
import com.goalmaster.task.data.entity.TaskWithData
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao : BaseDao<Task> {

    @Query("""SELECT * FROM task as t where t.goalId = :goalId order by updated_on desc""")
    fun observeGoalTasks(goalId: Long): Flow<List<Task>>

    @Query("SELECT * FROM Task WHERE id=:id")
    suspend fun findById(id: Long): Task?

    @Transaction
    @Query("SELECT * FROM Task WHERE id=:id")
    suspend fun findTaskWithDataById(id: Long): TaskWithData?

    @Transaction
    @Query("SELECT * FROM Task WHERE id=:id")
    fun observeTaskWithDataById(id: Long): Flow<TaskWithData?>

    @Query("DELETE FROM Task WHERE id=:taskId")
    suspend fun deleteById(taskId: Long)

    @Transaction
    @Query("SELECT * FROM Task WHERE state == 'UNPLANNED'")
    fun observeTasksForPlan(): Flow<List<TaskWithData>>
}