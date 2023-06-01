package com.goalmaster.task

import com.goalmaster.Result
import com.goalmaster.task.data.entity.Task
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

    suspend fun setTaskAsDone(task: Task): Result<Unit>
}