package com.goalmaster.task.data.source

import com.goalmaster.utils.Result
import com.goalmaster.task.data.entity.Task
import com.goalmaster.task.data.entity.TaskState
import com.goalmaster.task.data.entity.TaskTimeTracker
import com.goalmaster.task.data.entity.TaskWithData
import kotlinx.coroutines.flow.Flow

interface TaskDataSource {

    suspend fun saveTask(task: Task): Result<Unit>

    fun observeGoalTasks(goalId: Long): Flow<List<Task>>

    suspend fun getTask(taskId: Long): Result<Task>

    fun observeTaskWithData(taskId: Long): Flow<TaskWithData?>

    suspend fun getTaskWithData(taskId: Long): Result<TaskWithData>

    suspend fun deleteTask(taskId: Long): Result<Unit>

    fun observeTasksForPlan(): Flow<List<TaskWithData>>

    suspend fun updateTaskState(id: Long, state: TaskState): Result<Unit>

    suspend fun unPlanTask(task: Task): Result<Unit>

    suspend fun doneTask(task: Task): Result<Unit>

    fun observeRunningTaskTimeTracker(): Flow<TaskTimeTracker?>

    suspend fun saveTaskTimeTracker(ttt: TaskTimeTracker): Result<Unit>

}