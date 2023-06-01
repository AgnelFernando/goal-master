package com.goalmaster.task

import com.goalmaster.Result
import com.goalmaster.goal.data.source.LocalGoalDataSource
import com.goalmaster.task.data.entity.Task
import com.goalmaster.task.data.entity.TaskWithData
import kotlinx.coroutines.flow.Flow

class DefaultTaskRepository(
    private val dataSource: LocalTaskDataSource,
    private val goalDataSource: LocalGoalDataSource
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
            val taskResult = dataSource.getTaskWithData(id)
            taskResult as Result.Success
            val task = taskResult.data.task
            goalDataSource.updateCompletedUnits(taskResult.data.goal.id, task.unitSize)
            dataSource.setTaskAsDone(task)
            Result.Success(Unit)
        } catch (ex: Exception) {
            Result.Error(ex)
        }
    }
}