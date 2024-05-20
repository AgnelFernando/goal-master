package com.goalmaster.task

import com.goalmaster.utils.Result
import com.goalmaster.task.data.entity.Task
import com.goalmaster.task.data.entity.TaskState
import com.goalmaster.task.data.entity.TaskTimeTracker
import com.goalmaster.task.data.entity.TaskWithData
import com.goalmaster.task.data.source.LocalTaskDataSource
import com.goalmaster.task.data.source.TaskRepository
import kotlinx.coroutines.flow.Flow

class DefaultTaskRepository(
    private val dataSource: LocalTaskDataSource,
) : TaskRepository {

    override suspend fun saveTask(task: Task): Result<Unit> {
        return try {
            return dataSource.saveTask(task)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun observeTaskWithData(taskId: Long): Flow<TaskWithData?> {
        return dataSource.observeTaskWithData(taskId)
    }

    override suspend fun getTask(taskId: Long): Result<TaskWithData> {
        return try {
            dataSource.getTaskWithData(taskId)
        } catch (ex: Exception) {
            Result.Error(ex)
        }
    }

    override suspend fun deleteTask(taskId: Long): Result<Unit> {
        return try {
            return dataSource.deleteTask(taskId)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun observeTasksForPlan(): Flow<List<TaskWithData>> {
        return dataSource.observeTasksForPlan()
    }


    override suspend fun updateTaskState(id: Long, state: TaskState): Result<Unit> {
        return try {
            dataSource.updateTaskState(id, state)
            Result.Success(Unit)
        } catch (ex: Exception) {
            Result.Error(ex)
        }
    }

    override fun observeRunningTaskTimeTracker(): Flow<TaskTimeTracker?> {
        return dataSource.observeRunningTaskTimeTracker()
    }

    override suspend fun saveTimeTracker(ttt: TaskTimeTracker): Result<Unit> {
        return try {
            dataSource.saveTaskTimeTracker(ttt)
            Result.Success(Unit)
        } catch (ex: Exception) {
            Result.Error(ex)
        }
    }
}