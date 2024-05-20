package com.goalmaster.task.data.source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.goalmaster.database.BaseDao
import com.goalmaster.task.data.entity.Task
import com.goalmaster.task.data.entity.TaskTimeTracker
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
    @Query("SELECT t.* FROM Task t join Goal as " +
            "g on t.goalId=g.id WHERE t.state == 'UNPLANNED'" +
            " AND g.state='ACTIVE'")
    fun observeTasksForPlan(): Flow<List<TaskWithData>>

    @Transaction
    @Query("SELECT * FROM TaskTimeTracker WHERE endTime IS NULL")
    fun observeRunningTaskTimeTracker(): Flow<TaskTimeTracker?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskTimeTracker(ttt: TaskTimeTracker): Long
}