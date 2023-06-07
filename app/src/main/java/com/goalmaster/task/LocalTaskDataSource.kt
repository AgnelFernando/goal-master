package com.goalmaster.task

import com.goalmaster.Result
import com.goalmaster.goal.data.source.LocalGoalDataSource
import com.goalmaster.plan.data.source.PlanDao
import com.goalmaster.task.data.entity.Task
import com.goalmaster.task.data.entity.TaskWithData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class LocalTaskDataSource(
    private val taskDao: TaskDao,
    private val ioDispatcher: CoroutineDispatcher
) : TaskDataSource {

    override suspend fun saveTask(task: Task)
            : Result<Unit> = withContext(ioDispatcher) {
        return@withContext try {
            taskDao.insert(task)
            Result.Success(Unit)
        } catch (exception: Exception) {
            Result.Error(exception)
        }
    }

    override fun observeGoalTasks(goalId: Long): Flow<List<Task>> {
        return taskDao.observeGoalTasks(goalId)
    }

    override suspend fun getTask(taskId: Long): Result<Task> = withContext(ioDispatcher) {
        return@withContext try {
            val task = taskDao.findById(taskId)
            if (task == null) {
                Result.Error(Exception("Task not found!"))
            } else {
                Result.Success(task)
            }
        } catch (exception: Exception) {
            Result.Error(exception)
        }
    }

    override fun observeTaskWithData(taskId: Long): Flow<TaskWithData?> {
        return taskDao.observeTaskWithDataById(taskId)
    }

    override suspend fun getTaskWithData(taskId: Long): Result<TaskWithData> = withContext(ioDispatcher) {
        return@withContext try {
            val task = taskDao.findTaskWithDataById(taskId)
            if (task == null) {
                Result.Error(Exception("Task not found!"))
            } else {
                Result.Success(task)
            }
        } catch (exception: Exception) {
            Result.Error(exception)
        }
    }

    override suspend fun deleteTask(taskId: Long): Result<Unit> = withContext(ioDispatcher) {
        return@withContext try {
            val task = taskDao.deleteById(taskId)
            Result.Success(task)
        } catch (exception: Exception) {
            Result.Error(exception)
        }
    }

    override fun observeTasksForPlan(): Flow<List<TaskWithData>> {
        return taskDao.observeTasksForPlan()
    }

    override suspend fun updateTaskState(task: Task, state: TaskState): Result<Unit> = withContext(ioDispatcher) {
        return@withContext try {
            task.state = state
            if (task.state == TaskState.DONE) {
                taskDao.markPlanTaskCompletedByTaskId(task.id)
            }
            taskDao.insert(task)
            Result.Success(Unit)
        } catch (exception: Exception) {
            Result.Error(exception)
        }
    }
}