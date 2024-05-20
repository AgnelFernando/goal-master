package com.goalmaster.task.data.source

import com.goalmaster.utils.Result
import com.goalmaster.task.data.entity.Task
import com.goalmaster.task.data.entity.TaskState
import com.goalmaster.task.data.entity.TaskTimeTracker
import com.goalmaster.task.data.entity.TaskWithData
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    suspend fun saveTask(task: Task): Result<Unit>

    fun observeTaskWithData(taskId: Long): Flow<TaskWithData?>

    suspend fun getTask(taskId: Long): Result<TaskWithData>

    suspend fun deleteTask(taskId: Long): Result<Unit>

    fun observeTasksForPlan(): Flow<List<TaskWithData>>

    suspend fun updateTaskState(id: Long, state: TaskState): Result<Unit>

    fun observeRunningTaskTimeTracker(): Flow<TaskTimeTracker?>

    suspend fun saveTimeTracker(ttt: TaskTimeTracker): Result<Unit>
    
}