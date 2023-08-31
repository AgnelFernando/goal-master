package com.goalmaster.goal.data.source

import androidx.room.Dao
import androidx.room.Query
import com.goalmaster.database.BaseDao
import com.goalmaster.goal.data.entity.Goal
import com.goalmaster.goal.data.entity.GoalState
import com.goalmaster.task.data.entity.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao : BaseDao<Goal> {

    @Query("""SELECT * FROM goal as g WHERE state=:state order by dueDate""")
    fun observeActiveGoals(state: GoalState): Flow<List<Goal>>

    @Query("SELECT * FROM Goal WHERE id=:id")
    suspend fun findById(id: Long): Goal?

    @Query("""SELECT * FROM Task as t where t.goalId=:goalId order by created""")
    fun observeGoalTasks(goalId: Long): Flow<List<Task>>

    @Query("SELECT * FROM Goal WHERE id=:goalId")
    fun observeGoal(goalId: Long): Flow<Goal>

    @Query("DELETE FROM Goal WHERE id=:id")
    fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM Task as t where t.goalId=:goalId")
    fun getGoalTaskCount(goalId: Long): Int
}